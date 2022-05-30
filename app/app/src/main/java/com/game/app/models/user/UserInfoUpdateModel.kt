package com.game.app.models.user

import com.google.gson.annotations.SerializedName

data class UserInfoUpdateModel(
    @SerializedName("users_id") var usersId: String,
    @SerializedName("name") var name: String,
    @SerializedName("surname") var surname: String,
    @SerializedName("password") var password: String? = null
)