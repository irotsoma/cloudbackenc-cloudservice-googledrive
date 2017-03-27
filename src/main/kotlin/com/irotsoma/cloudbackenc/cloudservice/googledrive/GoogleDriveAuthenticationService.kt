
/*
 * Copyright (C) 2017  Irotsoma, LLC
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
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceAuthenticationRefreshListener
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceAuthenticationService
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceException
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceUser
import mu.KLogging
import java.io.File
import java.io.IOException
import java.net.URL

/**
 * Created by irotsoma on 6/19/2016.
 *
 * Authentication service implementation for Google Drive
 *
 * @author Justin Zak
 */

class GoogleDriveAuthenticationService : CloudServiceAuthenticationService {

    override var cloudServiceAuthenticationRefreshListener: CloudServiceAuthenticationRefreshListener? = null

    /**kotlin-logging implementation*/
    companion object: KLogging() {

        val credentialStorageLocation = File(System.getProperty("user.home"), ".credentials/cloudbackenc/googledrive")
        fun buildGoogleAuthorizationFlow(cloudServiceAuthenticationRefreshListener: CloudServiceAuthenticationRefreshListener?): GoogleAuthorizationCodeFlow {
            //make sure client ID and client secret are populated, otherwise the developer (probably you) forgot to add them
            if (GoogleDriveSettings.clientId == null || GoogleDriveSettings.clientSecret == null) {
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
            return GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, clientSecrets, listOf(DriveScopes.DRIVE_APPDATA)).setDataStoreFactory(dataStoreFactory).setAccessType("offline").addRefreshListener(GoogleCredentialRefreshListener(cloudServiceAuthenticationRefreshListener)).build()
        }
    }

    override fun isLoggedIn(cloudServiceUser: CloudServiceUser): Boolean {
        logger.info{"Google Drive isLoggedIn"}
        //TODO: Implement this
        return false
    }
    override fun login(cloudServiceUser: CloudServiceUser, cloudBackEncUser: CloudBackEncUser): CloudServiceUser.STATE {
        logger.info{"Google Drive Login"}
        //for integration testing
        if ((cloudBackEncUser.username == "test") || (cloudBackEncUser.roles.contains(CloudBackEncRoles.ROLE_TEST))){
            return CloudServiceUser.STATE.LOGGED_IN
        }
        //Verify that the user.serviceUUID is the same as the UUID for the current extension.
        if (cloudServiceUser.serviceUuid != GoogleDriveCloudServiceFactory.extensionUUID.toString()){
            throw CloudServiceException("The user object is invalid for this extension or the service UUID is incorrect.")
        }
        val flow = buildGoogleAuthorizationFlow(cloudServiceAuthenticationRefreshListener)

        //use a custom handler that will access the UI thread if the user needs to authorize.  This calls back to an embedded tomcat instance in the UI application.
        val handler = GoogleDriveAuthenticationCodeHandler(flow, LocalServerReceiver())
        try {
            handler.authorize(cloudBackEncUser.username, URL(cloudServiceUser.authorizationCallbackURL))
        }catch (e: IOException){
            throw CloudServiceException("Error during authorization process: ${e.message}", e)
        }

        return CloudServiceUser.STATE.AWAITING_AUTHORIZATION
    }
    override fun logoff(cloudServiceUser: CloudServiceUser): CloudServiceUser.STATE {
        logger.info{"Google Drive Logout"}

        //TODO: Implement this


        return CloudServiceUser.STATE.LOGGED_OUT
    }
}