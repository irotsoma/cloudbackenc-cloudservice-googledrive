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
 * Created by irotsoma on 8/9/2016.
 */
package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceCallbackURL
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceException
import mu.KLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import java.net.URL

/**
 * Modified AuthorizationCodeInstalledApp class which when an authorization URL is returned, calls the callback URL provided if applicable
 *
 * @author Justin Zak
 * @param flow An [AuthorizationCodeFlow] object to be used by the superclass
 * @param receiver A [VerificationCodeReceiver] object to be used by the superclass
 */
class GoogleDriveAuthenticationCodeHandler(flow: AuthorizationCodeFlow, receiver: VerificationCodeReceiver ) : AuthorizationCodeInstalledApp(flow, receiver) {
    /** kotlin-logging implementation*/
    companion object: KLogging()
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
            logger.debug{"Attempting callback to URL:  "+{authorizationCallbackUrl.toString()}}
            val currentAuthorizationURL = URL(authorizationUrl.build())
            logger.debug{"Google authorization URL:  "+{currentAuthorizationURL.toString()}}

            val restTemplate = RestTemplate()
            val requestHeaders = HttpHeaders()
            requestHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            val httpEntity = HttpEntity<CloudServiceCallbackURL>(CloudServiceCallbackURL(GoogleDriveCloudServiceFactory.extensionUUID.toString(), currentAuthorizationURL.toString()), requestHeaders)
            val callResponse = restTemplate.postForEntity(authorizationCallbackUrl.toString(), httpEntity, CloudServiceCallbackURL::class.java)
            logger.debug{"Callback response code:  "+callResponse.statusCode}
            logger.debug{"Callback response message:  "+callResponse.statusCodeValue}
            if (callResponse.statusCode != HttpStatus.ACCEPTED){
                throw CloudServiceException("Error accessing call back address for authorization URL:  ${callResponse.statusCode} -- ${callResponse.statusCodeValue}")
            }
        } else {
            super.onAuthorization(authorizationUrl)
        }
    }

    /**
     * Stores the callback url from the calling application and calls the superclass's authorize function.
     *
     * @param userID The user ID for the CloudBackEnc user
     * @param authorizationCallbackUrl The callback URL that will be used if the authorize call requires the user to navigate to an external site to finish the authorization process
     */
    fun authorize(userID:String, authorizationCallbackUrl: URL?) : Credential?{
        this.authorizationCallbackUrl = authorizationCallbackUrl
        return authorize(userID)
    }

}