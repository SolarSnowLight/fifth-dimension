const Router = require('express');
const router = new Router();
const { check } = require('express-validator');
const authManagementController = require('../controllers/auth-management-controller');
const AuthManagementApiConstants = require('../constants/addresses/server/auth-management-api-constants');

// Маршрут: /auth/management/login
router.post(
    AuthManagementApiConstants.login,
    [
        check('email', 'Некорректный почтовый адрес').isEmail(),
        check('password', 'Минимальная длина пароля должна быть 6 символов, а максимальная длина пароля - 32 символа')
    ],
    authManagementController.login
);

// Маршрут: /auth/logout
router.post(
    AuthManagementApiConstants.logout,
    [
        check('type_auth', 'Некорректные данные для выхода из системы').isNumeric()
    ],
    authManagementController.logout
);

// Маршрут: /auth/refresh
router.post(
    AuthManagementApiConstants.refresh,
    [
        check('type_auth', 'Некорректные данные для обновления токена доступа').isNumeric()
    ],
    authManagementController.refresh
);

module.exports = router;