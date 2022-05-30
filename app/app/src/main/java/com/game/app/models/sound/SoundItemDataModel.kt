package com.game.app.models.sound

import com.google.gson.annotations.SerializedName

data class SoundItemDataModel(
    @SerializedName("id"         ) var id        : Int?     = null,
    @SerializedName("subscribe"  ) var subscribe : Boolean? = null,
    @SerializedName("title"      ) var title     : String?  = null,
    @SerializedName("lesson_num" ) var lessonNum : Int?     = null,
    @SerializedName("title_img_path") var titleImgPath: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("type") val type: String? = null
)
