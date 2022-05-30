package com.game.app.models.auth

import com.google.gson.annotations.SerializedName

data class AuthRegisterModel(
    @SerializedName("email") var email : String,
    @SerializedName("password") var password : String,
    @SerializedName("name") var name : String,
    @SerializedName("surname") var surname : String,
)
