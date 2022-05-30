package com.game.app.models.course

import com.google.gson.annotations.SerializedName

data class CourseNoteDataModel(
    @SerializedName("courses_notes") var coursesNotes: ArrayList<CourseNoteItemModel>? = null
)