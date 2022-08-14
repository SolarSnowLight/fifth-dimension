package com.game.app.constants.network.auth

object AuthConstants {
    // Авторизация
    const val AUTH_LOGIN            = "/api/auth/login"

    // Регистрация
    const val AUTH_REGISTER         = "/api/auth/register"

    // Авторизация + регистрация (Google OAuth2)
    const val AUTH_OAUTH            = "/api/auth/oauth"

    // Обновление токена доступа
    const val AUTH_REFRESH_TOKEN    = "/api/auth/refresh/token"

    // Разлогирование пользователя
    const val AUTH_LOGOUT           = "/api/auth/logout"

    // Верификация авторизационных данных пользователя
    const val AUTH_VERIFICATION     = "/api/auth/verification"
}