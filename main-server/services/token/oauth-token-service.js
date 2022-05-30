/*
* Сервис для взаимодействия с OAuth2-токенами
*/

const fetch = require('node-fetch');
const GoogleApiConstants = require('../../constants/addresses/google/google-api-constants');
const { google } = require('googleapis');

class TokenServiceOAuth2{
    // Обновление токена доступа по токену обновления
    refreshAccessToken(refreshToken){
        // Определение объекта для взаимодействия с OAuth2
        const OAuth2Client = new google.auth.OAuth2(
            process.env.OAUTH_CLIENT_ID,
            process.env.OAUTH_SECRET_KEY,
            process.env.CLIENT_URL
        );

        // Конфигурирование объекта
        OAuth2Client.credentials.refresh_token = refreshToken;

        let accessToken = null;
        OAuth2Client.refreshAccessToken((error, tokens) => {
            if(!error){
                accessToken = tokens.access_token;
            }
        });

        return accessToken;
    }

    // Генерация пары токенов доступа и обновления
    async generateTokens(code){
        const OAuth2Client = new google.auth.OAuth2(
            process.env.OAUTH_CLIENT_ID,
            process.env.OAUTH_SECRET_KEY,
            process.env.CLIENT_URL
        );

        const { tokens } = await OAuth2Client.getToken(code);
        // await OAuth2Client.revokeToken(tokens.access_token);

        return {
            accessToken: tokens.access_token,
            refreshToken: tokens.refresh_token
        };
    }

    // Валидация токена доступа
    async validateAccessToken(token){
        try{
            let verifiedEmail = false;

            // Верификация почтового адреса пользователя по токену доступа
            await fetch(GoogleApiConstants.google_sequrity_oauth + token)
                .then(res => res.json())
                .then(json => {
                    verifiedEmail = json.verified_email;
                });
            
            if(!verifiedEmail){
                return false;
            }

            let data = {};

            // Получение данных пользователя по токену доступа
            await fetch(GoogleApiConstants.google_user_data + token)
                .then(res => res.json())
                .then(json => {
                    data = json;
                });

            return data;
        }catch(e){
            // Testing
            console.log(e);
            return false;
        }
    }

    // Удаление пары токенов доступа и обновления
    async removeTokenByAccessToken(accessToken){
        const OAuth2Client = new google.auth.OAuth2(
            process.env.OAUTH_CLIENT_ID,
            process.env.OAUTH_SECRET_KEY,
            process.env.CLIENT_URL
        );

        await OAuth2Client.revokeToken(accessToken);
    }
}

module.exports = new TokenServiceOAuth2();