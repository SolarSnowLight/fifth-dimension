package com.game.app.models.content

import com.google.gson.annotations.SerializedName
import java.time.Duration

data class AudioDataModel(
    @SerializedName("id") var id: Int,
    @SerializedName("lesson_num") var lessonNum: Int,
    @SerializedName("title") var title: String,
    @SerializedName("duration") var duration: String? = null,
    @SerializedName("title_img_path") var titleImgPath: String? = null,
    @SerializedName("sound_path") var soundPath: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("type") var type: String? = null,

    // Listening audio file
    @SerializedName("max_duration") var maxDuration: Int? = null,
    @SerializedName("current_duration") var currentDuration: Int = 0,
    @SerializedName("is_listening") var isListening: Boolean = false,

    // Loading audio file
    @SerializedName("loading") var loading: Boolean = true
)