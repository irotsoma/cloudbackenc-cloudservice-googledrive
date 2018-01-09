/*
 * Copyright (C) 2016-2018  Irotsoma, LLC
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
 * Created by irotsoma on 6/20/2016.
 */
package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.irotsoma.cloudbackenc.common.CloudBackEncRoles
import com.irotsoma.cloudbackenc.common.CloudBackEncUser
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceFile
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceFileIOService
import mu.KLogging
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.util.*

class GoogleDriveFileIOService(extensionUuid: UUID) : CloudServiceFileIOService(extensionUuid) {
    /** kotlin-logging implementation*/
    companion object: KLogging() {
        const val PARENT_DIRECTORY = "CloudBackEncFiles"
        const val MIME_TYPE = "application/octet-stream"
    }

    val flow = GoogleDriveAuthenticationService.buildGoogleAuthorizationFlow(null, extensionUuid)

    fun buildDrive(userId: String): Drive? {
        val credential = flow.loadCredential(userId)
        if (credential == null || (credential.refreshToken == null && credential.expiresInSeconds < 60)) {
            logger.info{"Credentials are invalid or about to expire.  New Login Required."}
            return null
        }
        return Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).build()
    }

    override fun delete(targetPath: String, user: CloudBackEncUser): Boolean {
        logger.info{"Google Drive delete called"}
        if ((user.username == "test") || (user.roles.contains(CloudBackEncRoles.ROLE_TEST))){
            GoogleDriveAuthenticationService.isTest=true
        }
        //TODO implement
        val drive = buildDrive(user.username) ?: return false
        throw UnsupportedOperationException("not implemented")



    }
    override fun upload(filePath: File, uploadedFilePath: Path, user: CloudBackEncUser): CloudServiceFile? {
        logger.trace{"Google Drive upload called"}
        if ((user.username == "test") || (user.roles.contains(CloudBackEncRoles.ROLE_TEST))){
            GoogleDriveAuthenticationService.isTest=true
        }
        val drive = buildDrive(user.username) ?: return null
        val driveFile = com.google.api.services.drive.model.File()
        driveFile.name = uploadedFilePath.fileName.toString()
        //check for and create any parent directories as needed
        val parents = ArrayList<String>(listOf(PARENT_DIRECTORY))
        for (x in 0..uploadedFilePath.nameCount-2){
            parents.add(uploadedFilePath.getName(x).toString())
        }
        driveFile.parents = parents
        driveFile.mimeType = MIME_TYPE
        val fileContent = FileContent(MIME_TYPE, filePath)
        val uploadedFile = drive.files().create(driveFile,fileContent).setFields("id").execute()

        return CloudServiceFile(uploadedFile.name, false, !uploadedFile.capabilities.canEdit, uploadedFile.capabilities.canCopy, uploadedFile.parents.toString(), uploadedFile.id, uploadedFile.size.toLong())
    }

    override fun list(dirPath: String, user: CloudBackEncUser): List<CloudServiceFile> {
        logger.info{"Google Drive list called"}
        if ((user.username == "test") || (user.roles.contains(CloudBackEncRoles.ROLE_TEST))){
            GoogleDriveAuthenticationService.isTest=true
        }
        //TODO implement
        throw UnsupportedOperationException("not implemented")

    }

    override fun download(filePath: String, user: CloudBackEncUser): InputStream {
        logger.info{"Google Drive download called"}
        if ((user.username == "test") || (user.roles.contains(CloudBackEncRoles.ROLE_TEST))){
            GoogleDriveAuthenticationService.isTest=true
        }
        //TODO implement
        throw UnsupportedOperationException("not implemented")
    }

    override fun availableSpace(user: CloudBackEncUser): Long {
        //TODO implement
        if ((user.username == "test") || (user.roles.contains(CloudBackEncRoles.ROLE_TEST))){
            GoogleDriveAuthenticationService.isTest=true
        }
        throw UnsupportedOperationException("not implemented")
    }
}