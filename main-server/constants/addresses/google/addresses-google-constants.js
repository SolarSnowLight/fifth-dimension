/*
* Константы адресов для взаимодействия с Google API
*/

const AddressesGoogleConstants = {
    google_sequrity_oauth: 'https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=', // Проверка токена в Google сервисах
    google_user_data: 'https://www.googleapis.com/oauth2/v2/userinfo?access_token=',       // Получение пользовательской информации
}

module.exports.AddressesGoogleConstants = AddressesGoogleConstants;