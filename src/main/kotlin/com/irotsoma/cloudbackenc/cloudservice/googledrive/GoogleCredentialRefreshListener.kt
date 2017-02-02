/**
 * Created by irotsoma on 2/1/17.
 */
package com.irotsoma.cloudbackenc.cloudservice.googledrive
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.CredentialRefreshListener
import com.google.api.client.auth.oauth2.TokenErrorResponse
import com.google.api.client.auth.oauth2.TokenResponse
import com.irotsoma.cloudbackenc.common.CloudBackEncUser
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceAuthenticationRefreshListener
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceUser

/**
 *
 *
 * @author Justin Zak
 */
class GoogleCredentialRefreshListener(val user: CloudBackEncUser, val changeListener:CloudServiceAuthenticationRefreshListener?) : CredentialRefreshListener {

    override fun onTokenErrorResponse(credential: Credential?, tokenErrorResponse: TokenErrorResponse?) {
        changeListener?.user = user
        changeListener?.onChange(CloudServiceUser.STATE.ERROR)
    }

    override fun onTokenResponse(credential: Credential?, tokenResponse: TokenResponse?) {
        changeListener?.user = user
        if (tokenResponse?.accessToken.isNullOrEmpty()) {
            changeListener?.onChange(CloudServiceUser.STATE.LOGGED_IN)
        } else {
            changeListener?.onChange(CloudServiceUser.STATE.LOGGED_OUT)
        }
    }
}