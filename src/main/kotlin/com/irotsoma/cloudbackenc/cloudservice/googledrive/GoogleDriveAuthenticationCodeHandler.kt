/*
 * Copyright (C) 2016-2019  Irotsoma, LLC
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
 * Created by irotsoma on 8/9/2016.
 */
package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceCallbackURL
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceException
import mu.KLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.net.URL
import java.util.*

/**
 * Modified AuthorizationCodeInstalledApp class which when an authorization URL is returned, calls the callback URL provided if applicable
 *
 * @author Justin Zak
 * @param flow An AuthorizationCodeFlow object to be used by the superclass
 * @param receiver A VerificationCodeReceiver object to be used by the superclass
 */
open class GoogleDriveAuthenticationCodeHandler(flow: AuthorizationCodeFlow, receiver: VerificationCodeReceiver, var extensionUuid:UUID ) : AuthorizationCodeInstalledApp(flow, receiver) {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    /**
     * The url of the calling application that will present the authorization URL to the user.
     */
    var authorizationCallbackUrl: URL? = null

    /**
     * If there is both an authorization URL and a callback URL then call the callback service rather than trying to open a local web browser or sending to System.out
     *
     * @param authorizationUrl  The URL that the user must browse to in order to complete the authorization process, if applicable.
     */
    override fun onAuthorization(authorizationUrl: AuthorizationCodeRequestUrl?) {
        if (authorizationUrl != null && authorizationCallbackUrl != null) {
            logger.debug{"Attempting callback to URL:  ${authorizationCallbackUrl.toString()}"}
            val currentAuthorizationURL = URL(authorizationUrl.build())
            logger.debug{"Google authorization URL:  $currentAuthorizationURL"}
            val restTemplate = RestTemplate()
            val requestHeaders = HttpHeaders()
            requestHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            val httpEntity = HttpEntity<CloudServiceCallbackURL>(CloudServiceCallbackURL(extensionUuid.toString(), currentAuthorizationURL.toString()), requestHeaders)
            val callResponse = restTemplate.postForEntity(authorizationCallbackUrl.toString(), httpEntity, CloudServiceCallbackURL::class.java)
            logger.debug{"Callback response:  ${callResponse.statusCode} -- ${callResponse.statusCode.name}"}
            if (callResponse.statusCode != HttpStatus.ACCEPTED){
                throw CloudServiceException("Error accessing call back address for authorization URL:  ${callResponse.statusCode} -- ${callResponse.statusCode.name}")
            }
        } else {
            super.onAuthorization(authorizationUrl)
        }
    }

    /**
     * Stores the callback url from the calling application and calls the superclass's authorize function.
     *
     * @param userId The user ID for the CloudBackEnc user
     * @param authorizationCallbackUrl The callback URL that will be used if the authorize call requires the user to navigate to an external site to finish the authorization process
     */
    fun authorize(userId:String, authorizationCallbackUrl: URL?) : Credential?{
        this.authorizationCallbackUrl = authorizationCallbackUrl
        return authorize(userId)
    }

    override fun authorize(userId:String):  Credential? {
        try {
            val credential = flow.loadCredential(userId)
            //if the credential is present and either it has a refresh token or it expires in more than 5 minutes then use the existing credential
            if (credential != null && (credential.refreshToken != null || credential.expiresInSeconds > 360)) {
                return credential
            }
            // open Google authorization page in browser
            val redirectUri = receiver.redirectUri
            val authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri)
            onAuthorization(authorizationUrl)
            // receive authorization code and exchange it for an access token
            logger.debug{"Waiting for user login."}
            val code = receiver.waitForCode()
            logger.debug{"Wait for code returned."}
            val response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute()
            //store credentials
            val outputCredential = flow.createAndStoreCredential(response, userId)
            //call all of the refresh listeners
            flow.refreshListeners?.forEach { it.onTokenResponse(outputCredential, response) }
            return outputCredential
        } catch (e:IOException){
            if (e.message?.contains("User authorization failed") == true){
                flow?.refreshListeners?.forEach{it?.onTokenErrorResponse(null, null)}
            }
            throw e
        }finally {
            receiver.stop()
        }
    }
}