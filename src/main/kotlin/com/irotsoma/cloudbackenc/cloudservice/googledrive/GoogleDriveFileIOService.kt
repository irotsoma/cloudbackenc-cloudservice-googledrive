package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.irotsoma.cloudbackenc.cloudservice.CloudServiceFileIOService
import java.io.File
import java.io.InputStream

/**
 * Created by irotsoma on 6/20/2016.
 */
class GoogleDriveFileIOService : CloudServiceFileIOService {
    override fun upload(filePath: File): Boolean {
        throw UnsupportedOperationException()
    }

    override fun list(dirPath: File): List<File> {
        throw UnsupportedOperationException()
    }

    override fun download(filePath: File): InputStream {
        throw UnsupportedOperationException()
    }
}