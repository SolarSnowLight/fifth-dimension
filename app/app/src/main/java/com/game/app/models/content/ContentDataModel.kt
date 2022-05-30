package com.game.app.models.content

import android.graphics.drawable.Drawable
import com.game.app.models.course.CourseSoundItemModel
import com.google.gson.annotations.SerializedName

data class ContentDataModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String,
    @SerializedName("title_img_path") var titleImgPath: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("count") var count: Int,
    @SerializedName("subscription") var subscription: Boolean,
    @SerializedName("description") var description: String? = null,
    @SerializedName("file_path") var filePath: String? = null,
    @SerializedName("sounds") var sounds: ArrayList<CourseSoundItemModel>? = null,
    @SerializedName("categoryId") var categoryId: Int? = null,
    @SerializedName("subcategoryId") var subcategoryId: Int? = null
)
