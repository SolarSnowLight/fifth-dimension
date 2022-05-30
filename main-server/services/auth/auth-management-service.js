const bcrypt = require('bcryptjs');
const tokenService = require('../token/token-service');
const tokenServiceJWT = require('../token/jwt-token-service');
const tokenServiceOAuth2 = require('../token/oauth-token-service');
const roleService = require('../role/role-service');
const AuthServiceValueConstants = require('../../constants/values/auth-service-value-constants');
const ApiError = require('../../exceptions/api-error');
const {
    UsersModel,
    TypeAuthModel,
} = require('../../data/index');


class AuthManagementService {
    // Авторизация пользователя
    async login(email, password){
        const user = await UsersModel.findOne({
            where: {
                email: email
            }
        });

        if(!user){
            throw ApiError.BadRequest(`Пользователь с почтовым адресом ${email} не найден`);
        }

        // Контроль изоляции авторизации и регистрации
        // Auth Services Check:
        // ---
        const typeAuth = await TypeAuthModel.findOne({
            where: {
                users_id: user.id
            }
        });

        if(typeAuth.type !== 0){
            throw ApiError.BadRequest(`Аккаунт с почтовым адресом ${email} должен авторизовываться через сервис ${AuthServiceValueConstants[typeAuth.type]}`);
        }
        // ---

        const roleUser = await roleService.getRoleUser(user.id);
        if((!roleUser.modules.admin) && (!roleUser.modules.dev)){
            throw ApiError.BadRequest(`Аккаунт с почтовым адресом ${email} не имеет доступа к сайту управления`);
        }

        // Проверка захэшированного пароля с введённым при авторизации
        const passwordValid = await bcrypt.compare(password, user.password);

        if(!passwordValid){
            throw ApiError.BadRequest("Неверный пароль");
        }

        const tokens = tokenServiceJWT.generateTokens({
            users_id: user.uuid
        });

        await tokenService.saveTokens(user.id, tokens.accessToken, tokens.refreshToken);

        return {
            tokens: {
                access_token: tokens.accessToken,
                refresh_token: tokens.refreshToken
            },
            modules: {
                // user: roleUser.modules.user,
                admin: roleUser.modules.admin,
                dev: roleUser.modules.dev
            },
            roles_id: roleUser.uuid,
            users_id: user.uuid,
            type_auth: typeAuth.type
        };
    }

    // Выход из системы
    async logout(userId, accessToken, refreshToken, typeAuth){
        const isLogout = await tokenService.removeTokens(userId, accessToken, refreshToken, typeAuth);

        return isLogout;
    }

    // Обновление токена доступа для аккаунта
    async refresh(refreshToken, typeAuth){

        let user = await tokenService.findUserByRefreshType(refreshToken, typeAuth);

        if(!user){
            throw ApiError.UnathorizedError();
        }

        // Check Type Auth:
        // ---
        const userTypeAuth = await TypeAuthModel.findOne({
            where: {
                users_id: user.id
            }
        });

        if((!userTypeAuth) || (userTypeAuth.type !== typeAuth)){
            throw ApiError.UnathorizedError();
        }
        // ---

        const roleUser = await roleService.getRoleUser(user.id);

        if((!roleUser.modules.admin) && (!roleUser.modules.dev)){
            throw ApiError.BadRequest("Данный аккаунт не имеет доступа к сайту управления");
        }

        let accessToken = null;

        switch(typeAuth){
            case 0: {
                accessToken = tokenServiceJWT.generateTokens({
                    users_id: user.uuid
                }).accessToken;

                break;
            }

            case 1: {
                accessToken = tokenServiceOAuth2.refreshAccessToken(refreshToken);
                break;
            }
        }

        if(!accessToken){
            throw ApiError.UnathorizedError();
        }

        await tokenService.saveTokens(user.id, accessToken, refreshToken);

        return {
            tokens: {
                access_token: accessToken,
                refresh_token: refreshToken
            },
            modules: {
                // user: roleUser.modules.user,
                admin: roleUser.modules.admin,
                dev: roleUser.modules.dev
            },
            roles_id: roleUser.uuid,
            users_id: user.uuid,
            type_auth: typeAuth
        };
    }
}

module.exports = new AuthManagementService();