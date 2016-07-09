package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.irotsoma.cloudbackenc.cloudservice.CloudServiceFileIOService
import java.io.File
import java.io.InputStream
import java.util.logging.Logger

/**
 * Created by irotsoma on 6/20/2016.
 */

class GoogleDriveFileIOService : CloudServiceFileIOService {

    val LOG = Logger.getLogger(this.javaClass.name)
    override fun upload(filePath: File): Boolean {
        LOG.info("Google Drive upload called")
        return true
    }

    override fun list(dirPath: File): List<File> {
        LOG.info("Google Drive list called")
        return listOf(File("test"))

    }

    override fun download(filePath: File): InputStream {
        LOG.info("Google Drive download called")
        throw UnsupportedOperationException()
    }
}