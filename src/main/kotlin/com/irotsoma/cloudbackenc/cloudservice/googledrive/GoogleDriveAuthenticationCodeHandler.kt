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

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceCallbackURL
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceException
import com.irotsoma.cloudbackenc.common.logger
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import java.net.URL

/**
 * Created by irotsoma on 8/9/2016.
 *
 * Modified AuthorizationCOdeInstalledApp class that when an authorization URL is returned, call the callback URL provided if applicable
 */
class GoogleDriveAuthenticationCodeHandler(flow: AuthorizationCodeFlow, receiver: VerificationCodeReceiver ) : AuthorizationCodeInstalledApp(flow,receiver) {
    companion object { val LOG by logger() }
    var authorizationURL: URL? = null
    var authorizationCallbackURL: URL? = null
    var serviceUUID: String? = null

    override fun onAuthorization(authorizationUrl: AuthorizationCodeRequestUrl?) {
        if (authorizationUrl != null && authorizationCallbackURL != null) {
            LOG.debug("Attempting callback to URL:  "+{authorizationCallbackURL.toString()})
            this.authorizationURL = URL(authorizationUrl.build())
            LOG.debug("Google authorization URL:  "+{this.authorizationURL.toString()})

            val restTemplate = RestTemplate()
            val requestHeaders = HttpHeaders()
            requestHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            val httpEntity = HttpEntity<CloudServiceCallbackURL>(CloudServiceCallbackURL(serviceUUID ?: "",this.authorizationURL.toString()), requestHeaders)
            val callResponse = restTemplate.postForEntity(authorizationCallbackURL.toString(), httpEntity, CloudServiceCallbackURL::class.java)
            LOG.debug("Callback response code:  "+callResponse.statusCode)
            LOG.debug("Callback response message:  "+callResponse.statusCodeValue)
            if (callResponse.statusCode != HttpStatus.ACCEPTED){
                throw CloudServiceException("Error accessing call back address for authorization URL:  ${callResponse.statusCode} -- ${callResponse.statusCodeValue}")
            }
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