package com.game.app.models.user

import com.google.gson.annotations.SerializedName

data class UserOpenAppModel(
    @SerializedName("first_open") var firstOpen: Boolean
)