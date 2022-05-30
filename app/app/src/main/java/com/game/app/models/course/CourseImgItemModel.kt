package com.game.app.models.course

import com.google.gson.annotations.SerializedName

data class CourseImgItemModel(
    @SerializedName("courses_id") var coursesId: Int,
    @SerializedName("filename") var filename: String,
    @SerializedName("file_path") var filePath: String
)