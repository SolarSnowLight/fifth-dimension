const { validationResult } = require('express-validator');
const authService = require('../services/auth/auth-service');
const ApiError = require('../exceptions/api-error');

class AuthController {
    // Регистрация пользователя
    async registration(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(ApiError.BadRequest('Некорректные регистрационные данные', errors.array()));
            }

            const { email, password, name, surname } = req.body;
            const data = await authService.registration(email, password, name, surname);

            return res.status(201).json(data);
        } catch (e) {
            next(e);
        }
    }

    // Авторизация пользователя
    async login(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(ApiError.BadRequest('Некорректные авторизационные данные', errors.array()));
            }

            const { email, password } = req.body;
            const data = await authService.login(email, password);

            return res.status(201).json(data);
        } catch (e) {
            next(e);
        }
    }

    // Выход из системы
    async logout(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(ApiError.BadRequest('Некорректные авторизационные данные', errors.array()));
            }

            const { users_id, access_token, refresh_token, type_auth } = req.body;
            const data = await authService.logout(users_id, access_token, refresh_token, Number(type_auth));

            return res.status(201).json(data);
        } catch (e) {
            next(e);
        }
    }

    // Активация аккаунта по ссылке
    async activate(req, res, next) {
        try {
            const activationLink = req.params.link;
            await authService.activate(activationLink);

            return res.redirect(process.env.API_URL + '/templates/account_confirmation.html');
        } catch (e) {
            next(e);
        }
    }

    // Обновление токена доступа
    async refresh(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(ApiError.BadRequest('Некорректные данные для обновления токена доступа', errors.array()));
            }

            const { refresh_token, type_auth } = req.body;

            const data = await authService.refresh(refresh_token, Number(type_auth));

            return res.status(201).json(data);
        } catch (e) {
            next(e);
        }
    }

    // Verification of authorization data (it is absolutely necessary to connect auth-middleware)
    async verification(req, res, next){
        try {
            // Returns a 401 code (see auth-middleware) if the user is not logged in (or the refresh token is no longer valid)
            return res.status(201).json({
                verification: true
            });
        } catch (e) {
            next(e);
        }
    }
}

module.exports = new AuthController();