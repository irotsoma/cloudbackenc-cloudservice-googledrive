package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.irotsoma.cloudbackenc.cloudservice.CloudServiceAuthenticationService
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceFactory
import com.irotsoma.cloudbackenc.cloudservice.CloudServiceFileIOService

/**
 * Created by irotsoma on 6/20/2016.
 *
 * Service Factory for Google Drive
 */

class GoogleDriveCloudServiceFactory : CloudServiceFactory {

    override var authenticationService: CloudServiceAuthenticationService = GoogleDriveAuthenticationService()
    override var cloudServiceFileIOService: CloudServiceFileIOService = GoogleDriveFileIOService()
}
