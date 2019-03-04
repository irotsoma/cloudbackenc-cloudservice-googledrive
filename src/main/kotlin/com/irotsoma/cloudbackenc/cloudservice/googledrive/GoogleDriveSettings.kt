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
 * Created by irotsoma on 7/15/2016.
 */
package com.irotsoma.cloudbackenc.cloudservice.googledrive

/**
 * This is a placeholder for the OAuth2 api client id and secret that must be received from Google.  There is a gradle
 * script to pull a copy of this file from a "secret" folder on build. You can then check the secret folder into a
 * private repo or only keep a local copy to prevent accidentally checking the id and secret into a public repo.
 *
 * I chose to do it this way instead of a properties file since decompiling is slightly more work than opening a jar
 * file for a "hacker" to get the key and secret.  Though this still isn't a safe way to distribute your api key.  You
 * should consider other methods if possible, or make sure not to distribute the extension outside of a server you
 * control.
 *
 * @author Justin Zak
 */
class GoogleDriveSettings{
    companion object {
        val clientId: String? = ""
        val clientSecret: String? = ""
        val authUri :String? = "https://accounts.google.com/o/oauth2/auth"
        val tokenUri: String? = "https://accounts.google.com/o/oauth2/token"
        val redirectUris: List<String> = listOf("urn:ietf:wg:oauth:2.0:oob","http://localhost")
    }
}