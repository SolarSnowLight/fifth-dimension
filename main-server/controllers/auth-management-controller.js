const { validationResult } = require('express-validator');
const authManagementService = require('../services/auth/auth-management-service');
const ApiError = require('../exceptions/api-error');

class AuthManagementController {
    // Авторизация пользователя
    async login(req, res, next){
        try{
            const errors = validationResult(req);
            
            if(!errors.isEmpty()){
                return next(ApiError.BadRequest('Некорректные авторизационные данные', errors.array()));
            }

            const { email, password } = req.body;
            const data = await authManagementService.login(email, password);

            return res.status(201).json(data);
        }catch(e){
            next(e);
        }
    }

    // Выход из системы
    async logout(req, res, next){
        try{
            const errors = validationResult(req);
            
            if(!errors.isEmpty()){
                return next(ApiError.BadRequest('Некорректные авторизационные данные', errors.array()));
            }

            const { users_id, access_token, refresh_token, type_auth } = req.body;
            const data = await authManagementService.logout(users_id, access_token, refresh_token, Number(type_auth));

            return res.status(201).json(data);
        }catch(e){
            next(e);
        }
    }

    // Обновление токена доступа
    async refresh(req, res, next){
        try{
            const errors = validationResult(req);
            
            if(!errors.isEmpty()){
                return next(ApiError.BadRequest('Некорректные данные для обновления токена доступа', errors.array()));
            }

            const { refresh_token, type_auth } = req.body;

            const data = await authManagementService.refresh(refresh_token, Number(type_auth));
            
            return res.status(201).json(data);
        }catch(e){
            next(e);
        }
    }
}

module.exports = new AuthManagementController();