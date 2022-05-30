/*
* Сервис для взаимодействия с JWT-токенами
*/

const jwt = require('jsonwebtoken');

class TokenServiceJWT{

    // Генерация пары токенов доступа и обновления
    generateTokens(payload){
        // Процесс генерации токена доступа
        const accessToken = jwt.sign(
            payload,                        // Данные
            process.env.JWT_ACCESS_SECRET,  // Секретный ключ
            {
                expiresIn: '1h'             // Время жизни токена
            }
        );

        const refreshToken = jwt.sign(
            payload,
            process.env.JWT_REFRESH_SECRET,
            {
                expiresIn: '30d'
            }
        );

        return {
            accessToken,
            refreshToken
        };
    }

    // Валидация токена доступа
    validateAccessToken(token){
        try{
            // Процесс верификации токена
            const data = jwt.verify(
                token,
                process.env.JWT_ACCESS_SECRET
            );

            // Возврат данных, содержащихся в токене, в случае успешной верификации
            return data;
        }catch(e){
            // Возврат false в случае неуспешной верификации токена
            return null;
        }
    }

    // Валидация токена обновления
    validateRefreshToken(token){
        try{
            const data = jwt.verify(
                token,
                process.env.JWT_REFRESH_SECRET
            );

            return data;
        }catch(e){
            return false;
        }
    }
}

module.exports = new TokenServiceJWT();