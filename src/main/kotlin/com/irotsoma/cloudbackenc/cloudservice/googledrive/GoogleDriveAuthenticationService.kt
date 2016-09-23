
/*
 * Copyright (C) 2016  Irotsoma, LLC
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
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.DriveScopes
import com.irotsoma.cloudbackenc.common.cloudservice.CloudServiceAuthenticationService
import com.irotsoma.cloudbackenc.common.cloudservice.CloudServiceException
import com.irotsoma.cloudbackenc.common.cloudservice.CloudServiceUser
import com.irotsoma.cloudbackenc.common.logger
import java.io.File
import java.io.IOException
import java.net.URL


/**
 * Created by irotsoma on 6/19/2016.
 *
 * Authentication service for Google Drive
 */

class GoogleDriveAuthenticationService : CloudServiceAuthenticationService {

    override var authorizationCallbackServiceURL: URL? = null

    companion object { val LOG by logger() }

    override fun isLoggedIn(user: CloudServiceUser): Boolean {
        LOG.info("Google Drive isLoggedIn")
        return false
    }
    override fun login(user: CloudServiceUser) : CloudServiceUser {
        LOG.info("Google Drive Login")
        //for integration testing
        if (user.userId == "test"){
            return CloudServiceUser(user.userId,"",user.serviceUUID, CloudServiceUser.STATE.LOGGED_IN,"")
        }

        val jsonFactory = JacksonFactory.getDefaultInstance()
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val secretData :GoogleClientSecrets.Details = GoogleClientSecrets.Details()

        //make sure client ID and client secret are populated, otherwise the developer (probably you) forgot to add them
        if (GoogleDriveSettings.clientId == null || GoogleDriveSettings.clientSecret == null){
            throw CloudServiceException("Google Drive client ID or secret is null.  This must be populated in the GoogleDriveSettings before building the plugin.")
        }
        //build Google secret details object
        secretData.clientId = GoogleDriveSettings.clientId
        secretData.clientSecret = GoogleDriveSettings.clientSecret
        secretData.authUri = GoogleDriveSettings.authUri
        secretData.tokenUri = GoogleDriveSettings.tokenUri
        secretData.redirectUris = GoogleDriveSettings.redirectUris
        val clientSecrets = GoogleClientSecrets()
        clientSecrets.installed=secretData
        //put a credential file in the user.home to hold credentials for future use.
        val dataStoreFactory = FileDataStoreFactory(File(System.getProperty("user.home"), ".credentials/cloudbackenc"))

        //use an offline access type to allow for getting a refresh key so the user doesn't need to authorize every time we connect
        val flow = GoogleAuthorizationCodeFlow.Builder(transport,jsonFactory,clientSecrets, listOf(DriveScopes.DRIVE_APPDATA)).setDataStoreFactory(dataStoreFactory).setAccessType("offline").build()
        //use a custom handler that will access the UI thread if the user needs to authorize.  This calls back to an embedded tomcat instance in the UI application.
        val handler = GoogleDriveAuthenticationCodeHandler(flow, LocalServerReceiver())
        try {
            handler.authorize(user.userId, user.serviceUUID, URL(user.authorizationCallbackURL))
        }catch (e: IOException){
            throw CloudServiceException("Error during authorization process: ${e.message}", e)
        }

        return CloudServiceUser(user.userId,"",user.serviceUUID, CloudServiceUser.STATE.LOGGED_IN, user.authorizationCallbackURL)
    }
    override fun logoff(user: CloudServiceUser) : String{
        LOG.info("Google Drive Logout")
        return "test logoff"
    }
}