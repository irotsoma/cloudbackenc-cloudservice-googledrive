/*
 * Copyright (C) 2016-2017  Irotsoma, LLC
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
import com.irotsoma.cloudbackenc.common.cloudservices.CloudServiceUser
import org.junit.Test

/**
 * @author Justin Zak
 */
class GoogleDriveAuthenticationServiceTest {
    @Test
    fun login() {

        val factory = GoogleDriveCloudServiceFactory()
        val loginState = factory.authenticationService.login(CloudServiceUser("test",null,"1d3cb21f-5b88-4b3c-8cb8-1afddf1ff375",null), CloudBackEncUser("test",CloudBackEncUser.PASSWORD_MASKED,null,true,listOf(CloudBackEncRoles.ROLE_TEST)))
        assert(loginState == CloudServiceUser.STATE.TEST)
    }

    @Test
    fun logoff() {

    }

}