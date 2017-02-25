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

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.irotsoma.cloudbackenc.common.CloudBackEncUser
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceFile
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceFileIOService
import com.irotsoma.cloudbackenc.common.logger
import java.io.File
import java.io.InputStream


class GoogleDriveFileIOService : CloudServiceFileIOService {

    companion object { val LOG by logger() }

    val flow = GoogleDriveAuthenticationService.buildGoogleAuthorizationFlow(null)

    fun buildDrive(userId: String): Drive? {
        val credential = flow.loadCredential(userId)
        if (credential == null || (credential.refreshToken == null && credential.expiresInSeconds < 60)) {
            LOG.info("Credentials are invalid or about to expire.  New Login Required.")
            return null
        }
        return Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).build()
    }

    override fun delete(targetPath: String, user: CloudBackEncUser): Boolean {
        LOG.info("Google Drive delete called")
        //TODO implement
        val drive = buildDrive(user.username) ?: return false
        throw UnsupportedOperationException("not implemented")



    }
    override fun upload(filePath: File, uploadedFilePath: String, user: CloudBackEncUser): Boolean {
        LOG.info("Google Drive upload called")
        //TODO implement
        throw UnsupportedOperationException("not implemented")
    }

    override fun list(dirPath: String, user: CloudBackEncUser): List<CloudServiceFile> {
        LOG.info("Google Drive list called")
        //TODO implement
        throw UnsupportedOperationException("not implemented")

    }

    override fun download(filePath: String, user: CloudBackEncUser): InputStream {
        LOG.info("Google Drive download called")
        //TODO implement
        throw UnsupportedOperationException("not implemented")
    }

    override fun availableSpace(user: CloudBackEncUser): Long {
        //TODO implement
        throw UnsupportedOperationException("not implemented")
    }
}