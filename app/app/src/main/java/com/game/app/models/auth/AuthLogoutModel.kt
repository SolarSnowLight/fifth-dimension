package com.game.app.models.auth

import com.google.gson.annotations.SerializedName

data class AuthLogoutModel(
    @SerializedName("users_id") var usersId: String,
    @SerializedName("access_token") var accessToken: String,
    @SerializedName("refresh_token") var refreshToken: String,
    @SerializedName("type_auth") var typeAuth: Int
)
