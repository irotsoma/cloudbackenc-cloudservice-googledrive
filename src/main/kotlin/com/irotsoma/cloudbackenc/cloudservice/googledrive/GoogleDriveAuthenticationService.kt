package com.irotsoma.cloudbackenc.cloudservice.googledrive

import java.util.logging.Logger
import com.irotsoma.cloudbackenc.cloudservice.AuthenticationService
import org.springframework.stereotype.Service

/**
 * Created by justin on 6/19/2016.
 *
 * Authentication service for Google Drive
 */
@Service("authenticationService")
class GoogleDriveAuthenticationService : AuthenticationService{


    val LOG = Logger.getLogger(this.javaClass.name)
    override fun login(username: String, password: String) : String{
        LOG.info("Google Drive Login")
        return ""
    }
    override fun logoff() : String{
        LOG.info("Google Drive Logout")
        return ""
    }
}