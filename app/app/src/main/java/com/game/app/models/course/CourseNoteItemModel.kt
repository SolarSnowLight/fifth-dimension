package com.game.app.models.course

import com.google.gson.annotations.SerializedName

data class CourseNoteItemModel(
    @SerializedName("courses_id") var coursesId : Int,
    @SerializedName("categoryId") var categoryId : Int,
    @SerializedName("subcategoryId") var subcategoryId : Int? = null,
    @SerializedName("title_img_path") var titleImgPath : String? = null
)
