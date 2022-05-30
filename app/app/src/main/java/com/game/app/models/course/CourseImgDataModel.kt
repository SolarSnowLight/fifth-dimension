package com.game.app.models.course

import com.google.gson.annotations.SerializedName

data class CourseImgDataModel(
    @SerializedName("title_images") var titleImages: ArrayList<CourseImgItemModel>
)