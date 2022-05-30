package com.game.app.models.sound

import com.google.gson.annotations.SerializedName

data class SoundDataModel(
    @SerializedName("sounds" ) var sounds : ArrayList<SoundItemDataModel> = arrayListOf()
)
