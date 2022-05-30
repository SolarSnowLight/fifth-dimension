package com.game.app.models.user

import com.google.gson.annotations.SerializedName

data class UserTokensModel(
    @SerializedName("access_token") var accessToken : String,
    @SerializedName("refresh_token") var refreshToken : String,
)
