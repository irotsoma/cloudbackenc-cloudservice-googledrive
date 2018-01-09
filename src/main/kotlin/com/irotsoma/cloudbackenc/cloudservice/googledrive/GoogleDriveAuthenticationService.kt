
/*
 * Copyright (C) 2016-2018  Irotsoma, LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
/*
 * Created by irotsoma on 6/19/2016.
 */
package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.DriveScopes
import com.irotsoma.cloudbackenc.common.CloudBackEncRoles
import com.irotsoma.cloudbackenc.common.CloudBackEncUser
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceAuthenticationRefreshListener
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceAuthenticationService
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceException
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceUser
import mu.KLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*

/**
 * Created by irotsoma on 6/19/2016.
 *
 * Authentication service implementation for Google Drive
 *
 * @author Justin Zak
 */

class GoogleDriveAuthenticationService(extensionUuid: UUID) : CloudServiceAuthenticationService(extensionUuid) {

    override var cloudServiceAuthenticationRefreshListener: CloudServiceAuthenticationRefreshListener? = null
    /**kotlin-logging implementation*/
    companion object: KLogging() {
        val credentialStorageLocation = File(System.getProperty("user.home"), ".credentials/cloudbackenc/googledrive")
        var isTest = false
        private val googleOauthRevokeUrl = "https://accounts.google.com/o/oauth2/revoke"
        fun buildGoogleAuthorizationFlow(cloudServiceAuthenticationRefreshListener: CloudServiceAuthenticationRefreshListener?,extensionUuid: UUID): GoogleAuthorizationCodeFlow {
            //make sure client ID and client secret are populated, otherwise the developer (probably you) forgot to add them
            if (GoogleDriveSettings.clientId == null || GoogleDriveSettings.clientSecret == null) {
                throw CloudServiceException("Google Drive client ID or secret is null.  This must be populated in the GoogleDriveSettings before building the extension.")
            }
            if ((GoogleDriveSettings.clientId == "" || GoogleDriveSettings.clientSecret == "") && !isTest){
                throw CloudServiceException("Google Drive client ID or secret is null.  This must be populated in the GoogleDriveSettings before building the extension.")
            }
            val jsonFactory = JacksonFactory.getDefaultInstance()
            val transport = GoogleNetHttpTransport.newTrustedTransport()
            val secretData = GoogleClientSecrets.Details()

            //build Google secret details object
            secretData.clientId = GoogleDriveSettings.clientId
            secretData.clientSecret = GoogleDriveSettings.clientSecret
            secretData.authUri = GoogleDriveSettings.authUri
            secretData.tokenUri = GoogleDriveSettings.tokenUri
            secretData.redirectUris = GoogleDriveSettings.redirectUris
            val clientSecrets = GoogleClientSecrets()
            clientSecrets.installed = secretData
            //create a credential file to hold credentials for future use
            val dataStoreFactory = FileDataStoreFactory(credentialStorageLocation)

            //use an offline access type to allow for getting a refresh key so the user doesn't need to authorize every time we connect
            return GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, clientSecrets, listOf(DriveScopes.DRIVE_APPDATA)).setDataStoreFactory(dataStoreFactory).setAccessType("offline").addRefreshListener(GoogleCredentialRefreshListener(cloudServiceAuthenticationRefreshListener, extensionUuid)).build()
        }
    }

    override fun isLoggedIn(cloudServiceUser: CloudServiceUser, cloudBackEncUser: CloudBackEncUser): Boolean {
        logger.info{"Google Drive isLoggedIn"}
        if ((cloudBackEncUser.username == "test") || (cloudBackEncUser.roles.contains(CloudBackEncRoles.ROLE_TEST))){
            isTest=true
        }
        //TODO: Implement this
        return false
    }
    override fun login(cloudServiceUser: CloudServiceUser, cloudBackEncUser: CloudBackEncUser): CloudServiceUser.STATE {
        logger.info{"Google Drive Login"}
        if ((cloudBackEncUser.username == "test") || (cloudBackEncUser.roles.contains(CloudBackEncRoles.ROLE_TEST))){
            isTest=true
        }
        val flow = buildGoogleAuthorizationFlow(cloudServiceAuthenticationRefreshListener,extensionUuid)
        //use a custom handler that will access the UI thread if the user needs to authorize.  This calls back to an embedded tomcat instance in the UI application.
        val handler = GoogleDriveAuthenticationCodeHandler(flow, LocalServerReceiver(), extensionUuid)
        //for integration testing
        if (isTest){
            return CloudServiceUser.STATE.TEST
        }
        //Verify that the user.serviceUUID is the same as the UUID for the current extension.
        if (cloudServiceUser.extensionUuid != extensionUuid.toString()){
            throw CloudServiceException("The user object is invalid for this extension or the service UUID is incorrect.")
        }
        try {
            val response = handler.authorize(cloudBackEncUser.username, URL(cloudServiceUser.authorizationCallbackURL))
            return if (response?.accessToken != null) {
                CloudServiceUser.STATE.LOGGED_IN
            } else {
                CloudServiceUser.STATE.ERROR
            }
        }catch (e: IOException){
            throw CloudServiceException("Error during authorization process: ${e.message}", e)
        }
    }
    override fun logout(cloudServiceUser: CloudServiceUser, cloudBackEncUser: CloudBackEncUser): CloudServiceUser.STATE {
        logger.info{"Google Drive Logout"}
        if ((cloudBackEncUser.username == "test") || (cloudBackEncUser.roles.contains(CloudBackEncRoles.ROLE_TEST))){
            isTest=true
        }
        //Verify that the user.serviceUUID is the same as the UUID for the current extension.
        if (cloudServiceUser.extensionUuid != extensionUuid.toString()){
            throw CloudServiceException("The user object is invalid for this extension or the service UUID is incorrect.")
        }
        val flow = buildGoogleAuthorizationFlow(cloudServiceAuthenticationRefreshListener,extensionUuid)
        val credential = flow.loadCredential(cloudBackEncUser.username)
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val request = HttpEntity<MultiValueMap<String, String>>(headers)
        val urlBuilder = UriComponentsBuilder.fromHttpUrl(googleOauthRevokeUrl)
                .queryParam("token",credential.accessToken)
        val url = urlBuilder.build().encode().toUri()
        try {
            val result = restTemplate.postForEntity(url, request, String::class.java)
            logger.debug { "Oauth revoke response code:  ${result.statusCode} -- ${result.statusCode?.name}" }
            logger.debug { "Oauth revoke response body:  ${result.body}" }
        } catch(e: HttpClientErrorException){
            logger.warn{"Error revoking access token:  ${e.message}"}
            logger.warn{ e.responseBodyAsString }
            //if access token is expired it will throw this error so try also revoking the refresh token
            //Note that the refresh token is explicitly revoked when an access token is successfully revoked so this is only necessary in this case.
            val urlBuilder2 = UriComponentsBuilder.fromHttpUrl(googleOauthRevokeUrl)
                    .queryParam("token",credential.refreshToken)
            val url2 = urlBuilder2.build().encode().toUri()
            try {
                val result = restTemplate.postForEntity(url2, request, String::class.java)
                logger.debug { "Oauth revoke response code:  ${result.statusCode} -- ${result.statusCode?.name}" }
                logger.debug { "Oauth revoke response body:  ${result.body}" }
            } catch(e: HttpClientErrorException){
                logger.warn{"Error revoking refresh token:  ${e.message}"}
                logger.warn{ e.responseBodyAsString }
                //if both revokes failed then just ignore it and delete the token locally
            }
        }
        flow.credentialDataStore?.delete(cloudBackEncUser.username)
        cloudServiceAuthenticationRefreshListener?.onChange(extensionUuid,CloudServiceUser.STATE.LOGGED_OUT)
        return CloudServiceUser.STATE.LOGGED_OUT
    }
}