package com.game.app.models.course

import com.google.gson.annotations.SerializedName

data class CourseItemModel(
    @SerializedName("id") var id : Int,
    @SerializedName("date_open") var dateOpen: String,
    @SerializedName("subscribe") var subscribe: Boolean,
    @SerializedName("description") var description: String,
    @SerializedName("type") var type: String? = null,
    @SerializedName("title") var title: String,
    @SerializedName("title_img_path") var titleImgPath: String? = null,
    @SerializedName("category") var categoryId : Int,
    @SerializedName("subcategory") var subcategoryId : Int?,
    @SerializedName("sounds") var sounds: ArrayList<CourseSoundItemModel>,
    @SerializedName("categoryTitle") var categoryTitle: String? = null
)