/*
* Repository for interaction with user authorization.
* The API used is passed to it as parameters
* and a reference to a data warehouse instance
* */

package com.game.app.repositories

import com.game.app.data.UserPreferences
import com.game.app.models.auth.AuthLoginModel
import com.game.app.models.auth.AuthRegisterModel
import com.game.app.network.apis.AuthApi
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class AuthRepository(
    private val api: AuthApi,
    private val preferences: UserPreferences
) : BaseRepository(){

    // Function for register user
    suspend fun login(
        email: String,
        password: String
    ) = safeApiCall {
        var requestBody = Gson().toJson(
            AuthLoginModel(
                email = email,
                password = password
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.authLogin(requestBody)
    }

    // Function for register user
    suspend fun register(
        email: String,
        password: String,
        name: String,
        surname: String
    ) = safeApiCall {
        var requestBody = Gson().toJson(
            AuthRegisterModel(
                email = email,
                password = password,
                name = name,
                surname = surname
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.authRegister(requestBody)
    }

    // Saving auth data
    suspend fun saveAuthData(authData: String){
        preferences.saveAuthData(authData)
    }
}