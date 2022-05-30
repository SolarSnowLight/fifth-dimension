package com.game.app.models.course

import com.google.gson.annotations.SerializedName

data class CourseSoundItemModel(
    @SerializedName("id") var id: Int,
    @SerializedName("lesson_num") var lessonNum: Int
)