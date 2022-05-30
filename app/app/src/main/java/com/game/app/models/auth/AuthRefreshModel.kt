package com.game.app.models.auth

import com.google.gson.annotations.SerializedName

data class AuthRefreshModel(
    @SerializedName("refresh_token") var refreshToken: String,
    @SerializedName("type_auth") var typeAuth: Int
)