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

import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceFile
import com.irotsoma.cloudbackenc.common.cloudservicesserviceinterface.CloudServiceFileIOService
import com.irotsoma.cloudbackenc.common.logger
import java.io.File
import java.io.InputStream




class GoogleDriveFileIOService : CloudServiceFileIOService {
    override fun delete(targetPath: String): Boolean {
        LOG.info("Google Drive delete called")
        throw UnsupportedOperationException("not implemented")
    }

    companion object { val LOG by logger() }

    override fun upload(filePath: File): Boolean {
        LOG.info("Google Drive upload called")
        return true
    }

    override fun list(dirPath: String): List<CloudServiceFile> {
        LOG.info("Google Drive list called")
        throw UnsupportedOperationException("not implemented")

    }

    override fun download(filePath: String): InputStream {
        LOG.info("Google Drive download called")
        throw UnsupportedOperationException()
    }
}