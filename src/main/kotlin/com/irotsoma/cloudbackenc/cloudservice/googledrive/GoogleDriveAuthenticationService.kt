package com.irotsoma.cloudbackenc.cloudservice.googledrive

import java.util.logging.Logger
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceAuthenticationService
import org.springframework.context.ApplicationContext


/**
 * Created by justin on 6/19/2016.
 *
 * Authentication service for Google Drive
 */

class GoogleDriveAuthenticationService(override var _applicationContext: ApplicationContext) : CloudServiceAuthenticationService {

    val LOG = Logger.getLogger(this.javaClass.name)
    override fun login(username: String, password: String) : String{
        LOG.info("Google Drive Login")
        return "test"
    }
    override fun logoff() : String{
        LOG.info("Google Drive Logout")
        return "test"
    }
}