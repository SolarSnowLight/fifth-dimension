/*
* Middleware для проверки токена доступа
*/

const ApiError = require('../../exceptions/api-error');
const tokenServiceJWT = require('../../services/token/jwt-token-service');
const tokenServiceOAuth2 = require('../../services/token/oauth-token-service');
const tokenService = require('../../services/token/token-service');
const userConverterService = require('../../services/user/user-converter-service');
const {
    UsersModel, TokensModel
} = require('../../data/index');

module.exports = async (req, res, next) => {
    try{
        const authorizationHeader = req.headers.authorization;

        if(!authorizationHeader){
            return next(ApiError.UnathorizedError());
        }

        const authSplit = authorizationHeader.split(' ');
        const accessToken = authSplit[2];

        if((authSplit.length != 3) || (!accessToken)){
            return next(ApiError.UnathorizedError());
        }

        let data = null;
        switch(Number(authSplit[1])){
            case 0: {
                data = tokenServiceJWT.validateAccessToken(accessToken);
                if(!data){
                    return next(ApiError.UnathorizedError());
                }
                
                data.users_id = await userConverterService.convertUsersUuidToId(data.users_id);
                if(!req.body.users_id){
                    req.body.users_id = data.users_id;
                }
                break;
            }

            case 1: {
                data = await tokenServiceOAuth2.validateAccessToken(accessToken);
                const user = await UsersModel.findOne({
                   where: {
                       email: data.email
                   } 
                });
                data.users_id = user.id;
                
                if(!req.body.users_id){
                    req.body.users_id = data.users_id;
                }
                break;
            }
        }

        const token = (data)? await tokenService.findIdAccessToken(data.users_id, accessToken) : null;
        
        if(!token){
            return next(ApiError.UnathorizedError());
        }

        next();
    }catch(e){
        console.log(e);
        return next(ApiError.UnathorizedError());
    }
}
