package com.game.app.models.content

import com.google.gson.annotations.SerializedName

data class AudioListDataModel(
    @SerializedName("audio_list") var audioList: ArrayList<AudioDataModel>? = null
)
