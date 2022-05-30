const {
    RolesModel, RolesModulesModel, UsersRolesModel,
    UsersModel
} = require('../../data/index');

class UserConverterService {
    async convertUsersUuidToId(usersId){
        usersId = (await UsersModel.findOne({
            where: {
                uuid: usersId
            }
        }));

        return (usersId)? usersId.id : null;
    }

    async convertRolesUuidToId(rolesId){
        rolesId = (await RolesModel.findOne({
            where: {
                uuid: rolesId
            }
        }));

        return (rolesId)? rolesId.id : null;
    }
}

module.exports = new UserConverterService();