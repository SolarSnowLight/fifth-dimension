/*
* Implementation of a class for interacting with the server,
* with logging and a 401 error interceptor (unauthorized)
* */

package com.game.app.network

import android.util.Log
import com.game.app.BuildConfig
import com.game.app.constants.network.auth.AuthConstants
import com.game.app.constants.network.main.MainNetworkConstants
import com.game.app.data.UserPreferences
import com.game.app.models.auth.AuthRefreshModel
import com.game.app.models.user.UserDataModel
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.IOException

class RemoteDataSource {
    // Function for build API
    fun <Api> buildApi(
        api: Class<Api>,
        userPreferences: UserPreferences?,
        authInterceptor: Boolean = true
    ): Api{
        // Use pattern Builder for create object for connected with networks
        return Retrofit.Builder()
            .baseUrl(MainNetworkConstants.SERVER_MAIN_ADDRESS)
            .client(OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor(userPreferences, authInterceptor))
                .also { client ->
                    // With BuildConfig with parameter DEBUG, retrofit will be write logs
                    if(BuildConfig.DEBUG){
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
                        client.addInterceptor(logging)
                    }
            }.build())
            .build()
            .create(api)
    }

    // Authorization interceptor for intercepting and responding to a 401 error
    internal class AuthorizationInterceptor(
        private val userPreferences: UserPreferences?,
        private val authInterceptor: Boolean = true
    ) : Interceptor{
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val original: Request = chain.request()

            // If authInterceptor equally null, then userPreferences equally null
            if(!authInterceptor){
                return chain.proceed(original)
            }

            val auth = Gson().fromJson(
                runBlocking {
                    userPreferences?.authData?.first()
                },
                UserDataModel::class.java
            )

            // Add in original request authorization access token
            var originalRequest: Request = original.newBuilder()
                 // Add header with access token and type auth
                .addHeader("Authorization", "Bearer ${auth.typeAuth} ${auth.tokens.accessToken}")
                .build()

            // Get define data
            var responseBody = chain.proceed(originalRequest)

            if(responseBody.code == 401){
                responseBody.close()

                val requestBody = Gson().toJson(
                    AuthRefreshModel(
                        refreshToken = auth.tokens.refreshToken,
                        typeAuth = auth.typeAuth
                    )
                ).toRequestBody("application/json".toMediaTypeOrNull())

                val request: Request = Request.Builder()
                    .url(MainNetworkConstants.SERVER_MAIN_ADDRESS + AuthConstants.AUTH_REFRESH_TOKEN)
                    .method("POST", requestBody)
                    .build()

                responseBody = chain.proceed(request)

                if(responseBody.code == 201){
                    val newAuth = Gson().fromJson(
                        Gson().toJson(
                            JsonParser.parseString(
                                responseBody.body?.string()
                            )
                        ),
                        UserDataModel::class.java
                    )

                    runBlocking {
                        userPreferences?.saveAuthData(
                            Gson().toJson(newAuth)
                        )
                    }

                    originalRequest = original.newBuilder()
                        .addHeader("Authorization", "Bearer ${newAuth.typeAuth} ${newAuth.tokens.accessToken}")
                        .build()

                    responseBody.close()
                    responseBody = chain.proceed(originalRequest)
                }
            }

            return responseBody
        }
    }
}