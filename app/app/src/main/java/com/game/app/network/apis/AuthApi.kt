package com.game.app.network.apis

import com.game.app.constants.network.auth.AuthConstants
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    // Function for auth user
    @POST(AuthConstants.AUTH_LOGIN)
    suspend fun authLogin(@Body requestBody: RequestBody): Response<ResponseBody>

    // Function for register user
    @POST(AuthConstants.AUTH_REGISTER)
    suspend fun authRegister(@Body requestBody: RequestBody): Response<ResponseBody>

    // Function for login with Google OAuth2
    @POST(AuthConstants.AUTH_OAUTH)
    suspend fun authGoogle(@Body requestBody: RequestBody): Response<ResponseBody>

    // Function for logout user
    @POST(AuthConstants.AUTH_LOGOUT)
    suspend fun authLogout(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST(AuthConstants.AUTH_VERIFICATION)
    suspend fun authVerification(): Response<ResponseBody>
}