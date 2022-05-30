package com.game.app.models.auth

import com.google.gson.annotations.SerializedName

data class AuthLoginModel(
    @SerializedName("email") var email : String,
    @SerializedName("password") var password : String
)
