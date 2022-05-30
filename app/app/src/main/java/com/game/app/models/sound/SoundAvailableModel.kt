package com.game.app.models.sound

import com.google.gson.annotations.SerializedName

data class SoundAvailableModel(
    @SerializedName("courses_id") var coursesId: Int?,
    @SerializedName("sounds_id") var soundsId: Int?,
    @SerializedName("users_id") var usersId: String?
)