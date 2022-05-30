package com.game.app.constants.network.auth

object AuthConstants {
    // Авторизация
    const val AUTH_LOGIN            = "/auth/login"

    // Регистрация
    const val AUTH_REGISTER         = "/auth/register"

    // Авторизация + регистрация (Google OAuth2)
    const val AUTH_OAUTH            = "/auth/oauth"

    // Обновление токена доступа
    const val AUTH_REFRESH_TOKEN    = "/auth/refresh/token"

    // Разлогирование пользователя
    const val AUTH_LOGOUT           = "/auth/logout"

    // Верификация авторизационных данных пользователя
    const val AUTH_VERIFICATION     = "/auth/verification"
}