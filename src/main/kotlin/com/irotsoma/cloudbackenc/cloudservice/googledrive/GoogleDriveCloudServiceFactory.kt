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
/*
 * Created by irotsoma on 6/20/2016.
 */
package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceAuthenticationService
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceExtensionConfig
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceFactory
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceFileIOService
import java.util.*

/**
 * The name of the resource file that contains the extension configuration
 */
private const val EXTENSION_CONFIG_FILE_PATH = "cloud-service-extension.json"
/**
 * Service Factory for Google Drive
 *
 * @author Justin Zak
 * @constructor Reads the config file to get the UUID and Name of the current extension.
 */

class GoogleDriveCloudServiceFactory : CloudServiceFactory {
    companion object {
        lateinit var extensionUUID: UUID
        lateinit var extensionName: String
    }
    constructor(){
        //get Json config file data
        val configFileStream = javaClass.classLoader.getResourceAsStream(EXTENSION_CONFIG_FILE_PATH)
        val jsonValue = configFileStream.reader().readText()
        val mapper = ObjectMapper().registerModule(KotlinModule())
        val mapperData: CloudServiceExtensionConfig = mapper.readValue(jsonValue)
        //add values to variables for consumption later
        extensionUUID = UUID.fromString(mapperData.serviceUUID)
        extensionName = mapperData.serviceName
    }
    override val authenticationService: CloudServiceAuthenticationService = GoogleDriveAuthenticationService()
    override val cloudServiceFileIOService: CloudServiceFileIOService = GoogleDriveFileIOService()
}
