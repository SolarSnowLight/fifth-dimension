const {
    RolesModel, RolesModulesModel, UsersRolesModel,
    UsersModel
} = require('../../data/index');

class RoleSecurityService {
    async checkAccess(usersId, rolesId, accessItem = 'admin'){
        let usersRoles = await UsersRolesModel.findOne({
            where: {
                users_id: usersId,
                roles_id: rolesId
            }
        });

        if(!usersRoles){
            return false;
        }

        const roles = await RolesModulesModel.findOne({
            where: {
                roles_id: rolesId
            }
        });

        return ((!roles) || (!roles[accessItem])) ? false : true;
    }
}

module.exports = new RoleSecurityService();