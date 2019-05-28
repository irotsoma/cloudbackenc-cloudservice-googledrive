/*
 * Copyright (C) 2016-2019  Irotsoma, LLC
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
import com.irotsoma.cloudbackenc.common.CloudBackEncUser
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceFile
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceFileIOService
import mu.KLogging
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.util.*

class GoogleDriveFileIOService(extensionUuid: UUID, private val additionalSettings: Map<String, String>) : CloudServiceFileIOService(extensionUuid) {
    /** kotlin-logging implementation*/
    private companion object: KLogging() {
        const val PARENT_DIRECTORY = "CloudBackEncFiles"
        const val FILE_MIME_TYPE = "application/octet-stream"
        const val FOLDER_MIME_TYPE = "application/vnd.google-apps.folder"
    }

    private fun buildDrive(userId: String): Drive? {
        val flow = GoogleDriveAuthenticationService.buildGoogleAuthorizationFlow(null, extensionUuid, additionalSettings)
        val credential = flow.loadCredential(userId)
        if (credential == null || (credential.refreshToken == null && credential.expiresInSeconds < 60)) {
            logger.info{"Credentials are invalid or about to expire.  New Login Required."}
            return null
        }
        return Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).build()
    }

    override fun delete(targetFile: CloudServiceFile, user: CloudBackEncUser): Boolean {
        logger.trace{"Google Drive delete called"}
        val drive = buildDrive(user.username) ?: return false
        if (targetFile.fileId != null){
            drive.files().delete(targetFile.fileId).execute()
            return true
        }
        throw UnsupportedOperationException("not implemented")

    }
    override fun upload(filePath: File, uploadedFilePath: Path, user: CloudBackEncUser): CloudServiceFile? {
        logger.trace{"Google Drive upload called"}
        val drive = buildDrive(user.username) ?: return null
        val driveFile = com.google.api.services.drive.model.File()
        driveFile.name = uploadedFilePath.fileName.toString()
        //check for and create any parent directories as needed
        val parents = ArrayList<String>(listOf(PARENT_DIRECTORY))
        for (x in 0..uploadedFilePath.nameCount-2){
            parents.add(uploadedFilePath.getName(x).toString())
        }
        //create an object holding all of the ids for all parent folders creating new ones if needed
        for (folder in parents){
            val file = drive.files().list().setSpaces("appDataFolder").setQ("mimeType = $FOLDER_MIME_TYPE and name = $folder").execute()
            if (file.files.isEmpty()){
                val newFolderMetadata = com.google.api.services.drive.model.File()
                newFolderMetadata.name=folder
                newFolderMetadata.mimeType = FOLDER_MIME_TYPE
                val newFolder = drive.files().create(newFolderMetadata).setFields("id").execute()
                driveFile.parents.add(newFolder.id)
            } else {
                driveFile.parents.add(file.files[0].id)
            }
        }
        driveFile.mimeType = FILE_MIME_TYPE
        val fileContent = FileContent(FILE_MIME_TYPE, filePath)
        val uploadedFile = drive.files().create(driveFile,fileContent).setFields("id").execute()

        return CloudServiceFile(uploadedFile.name, false, !uploadedFile.capabilities.canEdit, uploadedFile.capabilities.canCopy, uploadedFile.parents.toString(), uploadedFile.id, uploadedFile.size.toLong(), null)
    }

    override fun list(query: String, user: CloudBackEncUser): List<CloudServiceFile> {
        logger.trace{"Google Drive list called"}
        val drive = buildDrive(user.username) ?: return emptyList()
        val fileList = drive.files().list().setSpaces("appDataFolder").setQ("fullText contains '$query'").execute()
        val cloudServiceFileList = ArrayList<CloudServiceFile>()
        for (file in fileList.files) {
            val filePath = StringBuilder()
            for (parentId in file.parents){
                filePath.append(File.pathSeparator)
                filePath.append(drive.files().get(parentId).execute().name)
            }
            cloudServiceFileList.add(CloudServiceFile(file.name,file.mimeType == FOLDER_MIME_TYPE, !file.capabilities.canEdit, file.capabilities.canDownload, filePath.toString(), file.id, file.getSize(), null))
        }
        return cloudServiceFileList
    }

    override fun download(file: CloudServiceFile, user: CloudBackEncUser): InputStream {
        logger.trace{"Google Drive download called"}
        //TODO implement
        throw UnsupportedOperationException("not implemented")
    }

    override fun availableSpace(user: CloudBackEncUser): Long? {
        logger.trace{"Google Drive available space called"}
        val drive = buildDrive(user.username) ?: return null
        val storageQuota = drive.about().get().execute().storageQuota
        //missing limit means unlimited storage
        if (storageQuota.limit == null){
            return -1
        }
        val available = storageQuota.limit - storageQuota.usage
        //if user is over limit return 0 as amount available
        return if (available < 0){
            0
        } else {
            available
        }
    }
}