package com.game.app.utils.audio

import kotlin.math.floor

fun getDurationString(duration: Int): String{
    val durationSeconds = floor((duration / 1000).toDouble())

    val hours = floor(durationSeconds / 3600)
    val minutes = floor((durationSeconds - hours * 3600) / 60)
    val seconds = (durationSeconds - hours * 3600 - minutes * 60)

    var result = ""

    if(hours >= 10){
        result += "${hours.toInt()}:"
    }

    result += if(minutes < 10){
        "0${minutes.toInt()}:"
    }else{
        "${minutes.toInt()}:"
    }

    result += if(seconds < 10){
        "0${seconds.toInt()}"
    }else{
        "${seconds.toInt()}"
    }

    return result
}