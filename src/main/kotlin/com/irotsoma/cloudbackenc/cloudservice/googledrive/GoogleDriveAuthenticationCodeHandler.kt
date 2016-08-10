package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceException
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by irotsoma on 8/9/2016.
 *
 * Modified AuthorizationCOdeInstalledApp class that when an authorization URL is returned, call the callback URL provided if applicable
 */
class GoogleDriveAuthenticationCodeHandler(flow: AuthorizationCodeFlow, receiver: VerificationCodeReceiver ) : AuthorizationCodeInstalledApp(flow,receiver) {

    var authorizationURL: URL? = null
    var authorizationCallbackURL: URL? = null
    var serviceUUID: String? = null

    override fun onAuthorization(authorizationUrl: AuthorizationCodeRequestUrl?) {
        if (authorizationUrl != null && authorizationCallbackURL != null) {
            this.authorizationURL = URL(authorizationUrl.build())
            val connection: HttpURLConnection = (authorizationCallbackURL?.openConnection() ?: throw CloudServiceException("Error processing callback URL")) as HttpURLConnection
            connection.doOutput=true
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")

            val jsonUrl = "{\"uuid\":\""+"\"authorizationURL\":\""+this.authorizationURL.toString()+"\"}"

            val stream: OutputStream  = connection.outputStream
            stream.write(jsonUrl.toByteArray())
            stream.flush()

            if (connection.responseCode != HttpURLConnection.HTTP_OK){
                throw CloudServiceException("Error accessing call back address for authorization URL:  "+connection.responseCode+" -- "+connection.responseMessage)
            }
            connection.disconnect()
        } else {
            super.onAuthorization(authorizationUrl)
        }
    }

    fun authorize(userID:String, uuid: String, authorizationCallbackURL: URL?){
        serviceUUID = uuid
        this.authorizationCallbackURL = authorizationCallbackURL
        authorize(userID)
    }

}