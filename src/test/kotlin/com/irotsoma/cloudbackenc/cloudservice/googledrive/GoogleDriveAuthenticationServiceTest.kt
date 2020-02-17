/*
 * Copyright (C) 2016-2020  Irotsoma, LLC
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

package com.irotsoma.cloudbackenc.cloudservice.googledrive

import com.irotsoma.cloudbackenc.common.CloudBackEncRoles
import com.irotsoma.cloudbackenc.common.CloudBackEncUser
import com.irotsoma.cloudbackenc.common.UserAccountState
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceAuthenticationRequest
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceAuthenticationState
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.*

/**
 * @author Justin Zak
 */
class GoogleDriveAuthenticationServiceTest {


    //TODO: change build to not include secrets during test build and create test to make sure GoogleDriveSettings doesn't have the credentials in it

    @Test
    fun login() {

        val factory = GoogleDriveCloudServiceFactory()
        val loginState = factory.authenticationService.login(CloudServiceAuthenticationRequest("test",null,"1d3cb21f-5b88-4b3c-8cb8-1afddf1ff375",null), CloudBackEncUser("test",CloudBackEncUser.PASSWORD_MASKED,null,UserAccountState.ACTIVE,listOf(CloudBackEncRoles.ROLE_TEST)))
        assert(loginState.cloudServiceAuthenticationState == CloudServiceAuthenticationState.TEST)
    }

    @Test
    @Disabled
    fun realCredentialsLogin() {
        //TODO: find a way to set the credentials in the current user directory
        val factory = GoogleDriveCloudServiceFactory()
        //val test = this.javaClass.classLoader.getResource("TestCredentials.properties")

        val properties = Properties()
        properties.load(this.javaClass.classLoader.getResourceAsStream("TestCredentials.properties"))

        val testUser = properties.getProperty("username")
        if (testUser != null) {
            if (factory.authenticationService.isLoggedIn(CloudServiceAuthenticationRequest("test",null,"1d3cb21f-5b88-4b3c-8cb8-1afddf1ff375",null), CloudBackEncUser("test",CloudBackEncUser.PASSWORD_MASKED,null, UserAccountState.ACTIVE,listOf(CloudBackEncRoles.ROLE_TEST)))){
                assert(true)
            }
        }
        assert(false)
    }
}