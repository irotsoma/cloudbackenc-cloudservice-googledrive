package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.DriveScopes
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceAuthenticationService
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceUser
import com.irotsoma.cloudbackenc.common.logger
import java.io.File


/**
 * Created by irotsoma on 6/19/2016.
 *
 * Authentication service for Google Drive
 */

class GoogleDriveAuthenticationService : CloudServiceAuthenticationService {
    companion object { val LOG by logger() }

    override fun isLoggedIn(user: CloudServiceUser): Boolean {
        LOG.info("Google Drive isLoggedIn")
        return false
    }
    override fun login(user: CloudServiceUser) : String{
        LOG.info("Google Drive Login")
        //for integration testing
        if (user.userId == "test"){
            return "test login"
        }

        val jsonFactory = JacksonFactory.getDefaultInstance()
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val secretData :GoogleClientSecrets.Details = GoogleClientSecrets.Details()

        secretData.clientId = GoogleDriveSettings.clientId
        secretData.clientSecret = GoogleDriveSettings.clientSecret
        secretData.authUri = GoogleDriveSettings.authUri
        secretData.tokenUri = GoogleDriveSettings.tokenUri
        secretData.redirectUris = GoogleDriveSettings.redirectUris
        val clientSecrets = GoogleClientSecrets()
        clientSecrets.installed=secretData
        //val clientSecrets = GoogleClientSecrets.load(jsonFactory,InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("client_secret.json")))
        val dataSToreFactory = FileDataStoreFactory(File(System.getProperty("user.home"), ".credentials/cloudbackenc"))
        val flow = GoogleAuthorizationCodeFlow.Builder(transport,jsonFactory,clientSecrets, listOf(DriveScopes.DRIVE_APPDATA)).setDataStoreFactory(dataSToreFactory).setAccessType("offline").build()

        //TODO:  send a request back to the ui app to open the URL (override browse? or use another convenience class)

        val authCode = AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize(user.userId)


        return "test login"
    }
    override fun logoff(user: CloudServiceUser) : String{
        LOG.info("Google Drive Logout")
        return "test logoff"
    }
}