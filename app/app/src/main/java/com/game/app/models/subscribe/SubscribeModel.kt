package com.game.app.models.subscribe

import com.google.gson.annotations.SerializedName

data class SubscribeModel(
    @SerializedName("subscribe") var subscribe: Boolean = false
)
