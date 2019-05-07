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
 * Created by irotsoma on 2/1/17.
 */
package com.irotsoma.cloudbackenc.cloudservice.googledrive
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.CredentialRefreshListener
import com.google.api.client.auth.oauth2.TokenErrorResponse
import com.google.api.client.auth.oauth2.TokenResponse
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceAuthenticationRefreshListener
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceUser
import java.util.*

/**
 *
 *
 * @author Justin Zak
 */
class GoogleCredentialRefreshListener(private val changeListener:CloudServiceAuthenticationRefreshListener?, private val extensionUuid: UUID) : CredentialRefreshListener {

    override fun onTokenErrorResponse(credential: Credential?, tokenErrorResponse: TokenErrorResponse?) {
        changeListener?.onChange(extensionUuid, CloudServiceUser.STATE.ERROR)
    }

    override fun onTokenResponse(credential: Credential?, tokenResponse: TokenResponse?) {
        if (!tokenResponse?.accessToken.isNullOrEmpty()) {
            changeListener?.onChange(extensionUuid, CloudServiceUser.STATE.LOGGED_IN)
        } else {
            changeListener?.onChange(extensionUuid, CloudServiceUser.STATE.LOGGED_OUT)
        }
    }
}