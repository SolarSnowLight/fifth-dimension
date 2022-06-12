const bcrypt = require('bcryptjs');
const uuid = require('uuid');
const mailService = require('../mail/mail-service');
const tokenService = require('../token/token-service');
const tokenServiceJWT = require('../token/jwt-token-service');
const tokenServiceOAuth2 = require('../token/oauth-token-service');
const roleService = require('../role/role-service');
const RoleValueConstants = require('../../constants/values/role-value-constants');
const AuthServiceValueConstants = require('../../constants/values/auth-service-value-constants');
const userConverterService = require('../user/user-converter-service');
const ApiError = require('../../exceptions/api-error');
const {
    UsersModel, UsersDataModel, 
    ActivationsModel, TypeAuthModel,
    sequelize
} = require('../../data/index');

class AuthService {
    // Регистрация пользователя
    async registration(email, password, name, surname){
        const t = await sequelize.transaction();

        try{
            const candidat = await UsersModel.findOne({
                where: {
                    email: email
                }
            });
    
            if(candidat){
                throw ApiError.BadRequest(`Пользователь с почтовым адресом ${email} уже существует`);
            }
    
            // Хэширование пароля
            const hashPassword = await bcrypt.hash(password, 12);
    
            // Генерация ссылки активации аккаунта
            const activationLink = uuid.v4();
    
            // Создание нового пользователя
            const user = await UsersModel.create({
                email: email,
                password: hashPassword,
                uuid: uuid.v4()
            });
    
            // Добавление роли пользователю
            const modules = await roleService.setRole(user.id, RoleValueConstants.user);
    
            // Добавление информации о пользователе
            await UsersDataModel.create({
                users_id: user.id,
                name: name,
                surname: surname,
                date_register: (new Date()).toISOString().slice(0, 10)
            }, { transaction: t });
    
            await TypeAuthModel.create({
                users_id: user.id,
                type: 0
            }, { transaction: t });
    
            await ActivationsModel.create({
                users_id: user.id,
                activation_link: activationLink,
                is_activated: false
            }, { transaction: t });
    
            // Отправка сообщения на почту пользователя для активации аккаунта
            await mailService.sendActivationMail(email, `${process.env.API_URL}/api/auth/activate/${activationLink}`);

            await t.commit();

            const tokens = tokenServiceJWT.generateTokens({
                users_id: user.uuid
            });
        
            await tokenService.saveTokens(user.id, tokens.accessToken, tokens.refreshToken);

            return {
                tokens: {
                    access_token: tokens.accessToken,
                    refresh_token: tokens.refreshToken
                },
                /*modules: {
                    user: modules.user,
                    admin: modules.admin,
                    dev: modules.dev
                },*/
                roles_id: modules.roles_id,
                users_id: user.uuid,
                type_auth: 0
            };
        }catch(e) {
            await t.rollback();
        }
    }

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

        // Проверка захэшированного пароля с введённым при авторизации
        const passwordValid = await bcrypt.compare(password, user.password);

        if(!passwordValid){
            throw ApiError.BadRequest("Неверный пароль");
        }

        const tokens = tokenServiceJWT.generateTokens({
            users_id: user.uuid
        });

        await tokenService.saveTokens(user.id, tokens.accessToken, tokens.refreshToken);

        const roleUser = await roleService.getRoleUser(user.id);
    
        return {
            tokens: {
                access_token: tokens.accessToken,
                refresh_token: tokens.refreshToken
            },
            /*modules: (roleUser.modules.admin || roleUser.modules.dev)? {
                user: roleUser.modules.user,
                admin: roleUser.modules.admin,
                dev: roleUser.modules.dev
            } : undefined,*/
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

    // Активация аккаунта по ссылке
    async activate(activationLink){
        const userLink = await ActivationsModel.findOne({
            where: {
                activation_link: activationLink
            }
        });

        if(!userLink){
            throw ApiError.BadRequest("Не верная ссылка для активации аккаунта");
        }

        userLink.is_activated = true;
        await userLink.save();
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
        
        const roleUser = await roleService.getRoleUser(user.id);

        return {
            tokens: {
                access_token: accessToken,
                refresh_token: refreshToken
            },
            /*modules: (roleUser.modules.admin || roleUser.modules.dev)? {
                user: roleUser.modules.user,
                admin: roleUser.modules.admin,
                dev: roleUser.modules.dev
            } : undefined,*/
            roles_id: roleUser.uuid,
            users_id: user.uuid,
            type_auth: typeAuth
        };
    }
}

module.exports = new AuthService();