package com.game.app.constants.network.user

object CourseConstants{
    // Getting all courses of a certain category
    const val GET_COURSES_TITLE         = "/user/get/all/title"

    // Get all courses by date
    const val GET_COURSES_DATE          = "/user/get/all/date"

    // Getting an image of a specific course
    const val GET_COURSE_TITLE_IMAGE    = "/user/get/title/img"

    // Getting information about all audio files of a particular course
    const val GET_ALL_SOUNDS            = "/user/get/all/sounds"

    // Getting an image of a specific audio file
    const val GET_TITLE_IMG_SOUND      = "/user/get/title/img/sound"

    // Getting the audio file of each lesson
    const val GET_SOUND                 = "/user/get/sound"
}
