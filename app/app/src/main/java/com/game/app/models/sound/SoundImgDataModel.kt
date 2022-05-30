package com.game.app.models.sound

import com.google.gson.annotations.SerializedName

data class SoundImgDataModel(
    @SerializedName("title_images") var titleImages: ArrayList<SoundImgItemModel>
)
