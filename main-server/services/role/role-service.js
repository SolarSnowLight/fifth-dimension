const uuid = require('uuid');
const RoleValueConstants = require('../../constants/values/role-value-constants');
const {
    RolesModel, RolesModulesModel, UsersRolesModel
} = require('../../data/index');

class RoleService {
    // Get role of developer
    async getRoleDev(value) {
        let roleUser = await RolesModel.findOne({
            where: {
                value: value
            }
        });

        if (!roleUser) {
            roleUser = await RolesModel.create({
                value: value,
                description: 'Разработчик',
                uuid: uuid.v4(),
                users_id: null
            });

            await RolesModulesModel.create({
                roles_id: roleUser.id,
                user: true,
                admin: true,
                dev: true
            });
        }

        return roleUser;
    }

    // Ger role of user
    async getRole(value) {
        let roleUser = await RolesModel.findOne({
            where: {
                value: value
            }
        });

        if (!roleUser) {
            roleUser = await RolesModel.create({
                value: value,
                description: 'Пользователь',
                uuid: uuid.v4(),
                users_id: null
            });

            await RolesModulesModel.create({
                roles_id: roleUser.id,
                user: true,
                admin: false,
                dev: false
            });
        }

        return roleUser;
    }

    // Add role for user
    async setRole(userId, value) {
        let role = await this.getRole(value);

        try {
            if (role) {
                await UsersRolesModel.create({
                    users_id: userId,
                    roles_id: role.id
                });
            }else{
                const userRolesModules = await RolesModulesModel.findOne({
                    where: {
                        user: true,
                        admin: false,
                        dev: false
                    }
                });

                if(userRolesModules){
                    role = await RolesModel.findOne({
                        where: {
                            id: userRolesModules.roles_id
                        }
                    });

                    await UsersRolesModel.create({
                        users_id: userId,
                        roles_id: role.id
                    });
                }
            }
        } catch (e) {
            if (e.code === '23505') {
                // Reaction for error connected with ids
                await sequelize.query(`SELECT setval('users_roles_id_seq', (SELECT MAX(id) FROM "users_roles"))`);

                // Retry action
                await UsersRolesModel.create({
                    users_id: userId,
                    roles_id: role.id
                });
            }
        }

        const roleModules = await RolesModulesModel.findOne({
            where: {
                roles_id: role.id
            }
        });

        // Returns information about access modules for role
        return {
            user: roleModules.dataValues.user,
            admin: roleModules.dataValues.admin,
            dev: roleModules.dataValues.dev,
            roles_id: role.uuid,
        }
    }

    // [getRoleUser] Get role of user (algorithm)
    async getRoleUser(userId) {
        let usersRoles = await UsersRolesModel.findAll({
            where: {
                users_id: userId
            }
        });

        if (!usersRoles) {
            return null;
        }

        let roles = [];

        for (let i = 0; i < usersRoles.length; i++) {
            roles.push((await RolesModulesModel.findOne({
                where: {
                    roles_id: usersRoles[i].dataValues.roles_id
                }
            })).dataValues);
        }

        if((!roles) || (roles.length == 0)){
            // If not role, then set role and again operation for find roles
            await this.setRole(userId, RoleValueConstants.user);
            
            usersRoles = await UsersRolesModel.findAll({
                where: {
                    users_id: userId
                }
            });
    
            if (!usersRoles) {
                return null;
            }

            for (let i = 0; i < usersRoles.length; i++) {
                roles.push((await RolesModulesModel.findOne({
                    where: {
                        roles_id: usersRoles[i].dataValues.roles_id
                    }
                })).dataValues);
            }
        }

        roles.sort((role1, role2) => {

            const roleAdd = (role, title, value) => {
                return (role[title] == 1) ? value : 0;
            }

            const val1 = (roleAdd(role1, "user", 0) + roleAdd(role1, "admin", 2) + roleAdd(role1, "dev", 3));
            const val2 = (roleAdd(role2, "user", 0) + roleAdd(role2, "admin", 2) + roleAdd(role2, "dev", 3));

            if (val1 > val2) return -1;
            else if (val1 < val2) return 1;
            else 0;
        });

        const role = roles[0];

        const currentRole = (await RolesModel.findOne({
            where: {
                id: role.roles_id
            }
        })).dataValues;

        currentRole.modules = role;

        return {
            ...currentRole
        };
    }


    comparisonRoles(role1, role2) {
        const roleAdd = (role, title, value) => {
            return (role[title] == 1) ? value : 0;
        }

        const val1 = (roleAdd(role1, "user", 0) + roleAdd(role1, "admin", 2) + roleAdd(role1, "dev", 3));
        const val2 = (roleAdd(role2, "user", 0) + roleAdd(role2, "admin", 2) + roleAdd(role2, "dev", 3));

        return (val1 > val2) ? role1 : role2;
    }
    // [getRoleUser]
}

module.exports = new RoleService();