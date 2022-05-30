package com.game.app.models.sound

import com.google.gson.annotations.SerializedName

data class SoundImgItemModel(
    @SerializedName("sounds_id") var soundsId: Int,
    @SerializedName("filename") var filename: String,
    @SerializedName("file_path") var filePath: String,
    @SerializedName("subscribe") var subscribe: Boolean
)
