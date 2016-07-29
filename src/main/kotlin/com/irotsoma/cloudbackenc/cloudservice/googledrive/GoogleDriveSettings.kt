package com.irotsoma.cloudbackenc.cloudservice.googledrive

/**
 * Created by irotsoma on 7/15/2016.
 *
 * This is a placeholder for the OAuth2 api client id and secret that must be received from Google.  There is a gradle
 * script to pull a copy of this file from a "secret" folder on build. You can then check the secret folder into a
 * private repo or only keep a local copy to prevent accidentally checking the id and secret into a public repo.
 *
 * I chose to do it this way instead of a properties file since decompiling is slightly more work than opening a jar
 * file for a "hacker" to get the key and secret.  Though this still isn't a safe way to distribute your api key.  You
 * should consider other methods if possible, or make sure not to distribute the extension outside of a server you
 * control.
 */
class GoogleDriveSettings{
    companion object {
        val apiClientId: String? = null
        val apiSecret: String? = null
    }
}