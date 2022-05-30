package com.game.app.models.calendar

import com.google.gson.annotations.SerializedName

data class CalendarItemDataModel(
    @SerializedName("number") var number: Int,
    @SerializedName("day_week") var dayWeek: String,
    @SerializedName("count_new") var countNew: String
)
