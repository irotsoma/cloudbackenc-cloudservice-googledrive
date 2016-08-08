package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.irotsoma.cloudbackenc.cloudservice.CloudServiceAuthenticationService
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceUser
import com.irotsoma.cloudbackenc.common.logger


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
        return "test login"
    }
    override fun logoff(user: CloudServiceUser) : String{
        LOG.info("Google Drive Logout")
        return "test logoff"
    }
}