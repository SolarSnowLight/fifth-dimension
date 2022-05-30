package com.game.app.models.sound

import com.google.gson.annotations.SerializedName

data class SoundFileDataModel(
    @SerializedName("sound_files") var soundFiles: ArrayList<SoundFileItemModel>
)