package com.game.app.constants.network.user

object CourseConstants{
    // Getting all courses of a certain category
    const val GET_COURSES_TITLE         = "/api/user/get/all/title"

    // Get all courses by date
    const val GET_COURSES_DATE          = "/api/user/get/all/date"

    // Getting an image of a specific course
    const val GET_COURSE_TITLE_IMAGE    = "/api/user/get/title/img"

    // Getting information about all audio files of a particular course
    const val GET_ALL_SOUNDS            = "/api/user/get/all/sounds"

    // Getting an image of a specific audio file
    const val GET_TITLE_IMG_SOUND      = "/api/user/get/title/img/sound"

    // Getting the audio file of each lesson
    const val GET_SOUND                 = "/api/user/get/sound"
}
