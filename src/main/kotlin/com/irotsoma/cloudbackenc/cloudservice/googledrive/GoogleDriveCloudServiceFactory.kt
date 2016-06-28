package com.irotsoma.cloudbackenc.cloudservice.googledrive;

import com.irotsoma.cloudbackenc.cloudservice.AuthenticationService
import com.irotsoma.cloudbackenc.cloudservice.BaseCloudServiceFactory
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceFileIOService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by irotsoma on 6/20/2016.
 *
 * Service Factory for Google Drive
 */
@Service("cloudService")
class GoogleDriveCloudServiceFactory : BaseCloudServiceFactory() {
    override val serviceName: String = "Google Drive"
    @Autowired
    override lateinit var authenticationService: AuthenticationService
    @Autowired
    override lateinit var cloudServiceFileIOService: CloudServiceFileIOService

}
