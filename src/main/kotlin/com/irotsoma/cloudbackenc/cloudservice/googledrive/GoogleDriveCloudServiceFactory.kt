package com.irotsoma.cloudbackenc.cloudservice.googledrive;

import com.irotsoma.cloudbackenc.cloudservice.CloudServiceAuthenticationService
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceException
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceFactory
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceFileIOService
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service



/**
 * Created by irotsoma on 6/20/2016.
 *
 * Service Factory for Google Drive
 */
@Service("googleDriveCloudServiceFactory")
open class GoogleDriveCloudServiceFactory : CloudServiceFactory {
    lateinit var _applicationContext : ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext?) {
        _applicationContext = applicationContext ?: throw CloudServiceException("Missing application context when initializing cloud service factory.")
    }
    override val serviceName = "Google Drive"
    override var authenticationService: CloudServiceAuthenticationService = GoogleDriveAuthenticationService(_applicationContext)
    override var cloudServiceFileIOService: CloudServiceFileIOService = GoogleDriveFileIOService(_applicationContext)
}
