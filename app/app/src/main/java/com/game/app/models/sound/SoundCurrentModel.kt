package com.game.app.models.sound

import com.google.gson.annotations.SerializedName

data class SoundCurrentModel(
    @SerializedName("file_path") var filePath: String?,
    @SerializedName("position") var position: Int
)
