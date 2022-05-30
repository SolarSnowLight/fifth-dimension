package com.game.app.models.course

import com.google.gson.annotations.SerializedName

data class CourseDataModel(
    @SerializedName("courses") var courses : ArrayList<CourseItemModel>
)