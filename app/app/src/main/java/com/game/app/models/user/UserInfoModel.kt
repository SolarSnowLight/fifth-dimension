package com.game.app.models.user

import com.google.gson.annotations.SerializedName

data class UserInfoModel(
    @SerializedName("name") var name: String,
    @SerializedName("surname") var surname: String,
    @SerializedName("email") var email: String
)