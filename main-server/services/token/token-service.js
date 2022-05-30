/*
* Сервис токенов для взаимодействия с базой данных
*/

const {
    TokensModel, TypeAuthModel, UsersModel
} = require('../../data/index');

class TokenService{
    // Сохранение пары токенов доступа и обновления для определённого пользователя
    async saveTokens(userId, accessToken, refreshToken){
        let token = await TokensModel.findOne({
            where: {
                users_id: userId,
                refresh_token: refreshToken
            }
        });

        if(token){
            token.access_token = accessToken;
            token.refresh_token = refreshToken;

            return await token.save();
        }

        token = await TokensModel.create({
            users_id: userId,
            access_token: accessToken,
            refresh_token: refreshToken
        });

        return token;
    }

    // Поиск записи в таблице токенов по токену обновления и пользовательскому ID
    async findIdRefreshToken(userId, refreshToken){
        const token = await TokensModel.findOne({
            where: {
                users_id: userId,
                refresh_token: refreshToken
            }
        });

        return token;
    }

    // Поиск записи в таблице токенов по пользовательскому ID и токену обновления
    async findIdAccessToken(userId, accessToken){
        const token = await TokensModel.findOne({
            where: {
                users_id: userId,
                access_token: accessToken
            }
        });

        return token;
    }

    // Поиск записи в таблице токенов по пользовательскому ID
    async findIdToken(userId){
        const token = await TokensModel.findOne({
            where: {
                users_id: userId
            }
        });

        return token;
    }

    // Определение пользователя по токену обновления и типу авторизации
    async findUserByRefreshType(refreshToken, typeAuth){
        let token = await TokensModel.findOne({
            where: {
                refresh_token: refreshToken
            }
        });

        if(token){
            const auth = await TypeAuthModel.findOne({
                where: {
                    type: typeAuth,
                    users_id: token.users_id
                }
            });

            if(!auth){
                return null;
            }
        }else{
            return null;
        }

        const data = await UsersModel.findOne({
            where: {
                id: token.users_id
            }
        });

        return {
            id: data.id,
            email: data.email,
            uuid: data.uuid
        };
    }

    // Определение существует ли пользователь с определёнными аутентификационными данными
    async isUserExists(userId, accessToken, refreshToken, typeAuth){
        const token = await TokensModel.findOne({
           where: {
               users_id: userId,
               access_token: accessToken,
               refresh_token: refreshToken
           } 
        });

        const auth = await TypeAuthModel.findOne({
            where: {
                users_id: userId,
                type: typeAuth
            }
        });

        return (token && auth);
    }

    async getUserExists(userId, accessToken, refreshToken, typeAuth){
        const token = await TokensModel.findOne({
            where: {
                users_id: userId,
                access_token: accessToken,
                refresh_token: refreshToken
            } 
         });
 
         const auth = await TypeAuthModel.findOne({
             where: {
                 users_id: userId,
                 type: typeAuth
             }
         });

         if((!token) || (!auth)){
             return null;
         }
 
         return token;
    }

    // Удаление данных пользовательской сессии
    async removeTokens(userId, accessToken, refreshToken, typeAuth){
        const token = await this.getUserExists(userId, accessToken, refreshToken, typeAuth);

        if(token){
            await token.destroy();
            return true;
        }

        return false;
    }
}

module.exports = new TokenService();