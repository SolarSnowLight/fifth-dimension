const Router = require('express');
const router = new Router();
const { check } = require('express-validator');
const authController = require('../controllers/auth-controller');
const AuthApiConstants = require('../constants/addresses/server/auth-api-constants');
const authMiddleware = require('../middlewares/auth/auth-middleware');

// Маршрут: /auth/registration
router.post(
    AuthApiConstants.register,
    [
        check('email', 'Некорректный почтовый адрес').isEmail(),
        check('password', 'Минимальная длина пароля должна быть 6 символов, а максимальная длина пароля - 32 символа')
            .isLength({ min: 6, max: 32 }),
        check('name', 'Имя не может содержать меньше двух символов')
            .isLength({ min: 2 }),
        check('surname', 'Фамилия не может содержать меньше двух символов')
            .isLength({ min: 2 })
    ],
    authController.registration
);

// Маршрут: /auth/login
router.post(
    AuthApiConstants.login,
    [
        check('email', 'Некорректный почтовый адрес').isEmail(),
        check('password', 'Минимальная длина пароля должна быть 6 символов, а максимальная длина пароля - 32 символа')
    ],
    authController.login
);

// Маршрут: /auth/logout
router.post(
    AuthApiConstants.logout,
    [
        check('type_auth', 'Некорректные данные для выхода из системы').isNumeric()
    ],
    authController.logout
);

// Маршрут: /auth/activate
router.get(
    AuthApiConstants.activate,
    authController.activate
);

// Маршрут: /auth/refresh/token
router.post(
    AuthApiConstants.refresh,
    [
        check('type_auth', 'Некорректные данные для обновления токена доступа').isNumeric()
    ],
    authController.refresh
);

// Маршрут: /auth/verification
router.post(
    AuthApiConstants.verification,
    [
        // Auth-middleware is required here
        authMiddleware
    ],
    authController.verification
);

module.exports = router;