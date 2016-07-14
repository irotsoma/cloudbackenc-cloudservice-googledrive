package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.irotsoma.cloudbackenc.cloudservice.CloudServiceAuthenticationService
import com.irotsoma.cloudbackenc.common.logger


/**
 * Created by justin on 6/19/2016.
 *
 * Authentication service for Google Drive
 */

class GoogleDriveAuthenticationService : CloudServiceAuthenticationService {
    companion object { val LOG by logger() }

    override fun login(username: String, password: String) : String{
        LOG.info("Google Drive Login")
        return "test login"
    }
    override fun logoff() : String{
        LOG.info("Google Drive Logout")
        return "test logoff"
    }
}