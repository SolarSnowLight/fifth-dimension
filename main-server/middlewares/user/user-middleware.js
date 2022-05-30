/*
* Middleware для преобразования id пользователя и его роли во внутренний формат данных
*/

const ApiError = require('../../exceptions/api-error');
const {
    UsersModel, RolesModel
} = require('../../data/index');

module.exports = async (req, res, next) => {
    try{
        
        let { users_id, roles_id } = req.body;

        if(users_id){
            const data = await UsersModel.findOne({
                where: {
                    uuid: users_id
                }
            });

            if(!data){
                return next(ApiError.UnathorizedError());
            }

            users_id = data.id;
            req.body.users_id = users_id;
        }

        if(roles_id){
            const data = await RolesModel.findOne({
                where: {
                    uuid: roles_id
                }
            });

            if(!data){
                return next(ApiError.UnathorizedError());
            }

            roles_id = data.id;
            req.body.roles_id = roles_id;
        }

        next();
    }catch(e){
        return next(ApiError.UnathorizedError());
    }
}
