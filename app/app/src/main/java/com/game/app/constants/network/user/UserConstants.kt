package com.game.app.constants.network.user

// Prefix for all users constants
private const val USER_CONSTANTS_PREFIX = "/user"

object UserConstants {
    // Получение информации о пользователе
    const val USER_INFO    = "$USER_CONSTANTS_PREFIX/info"

    // Update users info
    const val UPDATE_INFO  = "$USER_CONSTANTS_PREFIX/info/update"

    // Добавление информации о прохождении одного урока
    const val COMPLETE_SOUND = "$USER_CONSTANTS_PREFIX/complete/sound"

    // Get new courses
    const val NEW_COURSES = "$USER_CONSTANTS_PREFIX/get/new/courses"

    // Check subscribe
    const val CHECK_SUBSCRIBE = "$USER_CONSTANTS_PREFIX/check/subscribe"
}