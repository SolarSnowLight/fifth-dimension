const ApiError = require('../../exceptions/api-error');
const UserInfoDto = require('../../dtos/user/user-info-dto');
const {
    RolesModel, RolesModulesModel, UsersRolesModel,
    UsersModel, UsersDataModel
} = require('../../data/index');
const bcrypt = require('bcryptjs');

class UserService {
    async getUserInfo(usersId) {
        const userData = await UsersDataModel.findOne({
            where: {
                users_id: usersId
            },

            include: {
                model: UsersModel,
                required: false,
                where: {
                    id: usersId
                }
            }
        });

        if (!userData) {
            throw ApiError.BadRequest("Информации о данном пользователе нет");
        }

        return {
            ...(new UserInfoDto(userData)),
            email: userData.user.dataValues.email
        }
    }

    async updateUserInfo(usersId, name, surname, password) {
        const userInfoData = await UsersDataModel.findOne({
            where: {
                users_id: usersId
            }
        });

        const userData = await UsersModel.findOne({
            where: {
                id: usersId
            }
        });

        userInfoData.set({
            name: name,
            surname: surname
        });

        await userInfoData.save();

        if (password) {
            const hashPassword = await bcrypt.hash(password, 12);
            userData.set({
                password: hashPassword
            });

            await userData.save();
        }

        return {
            ...(new UserInfoDto(userInfoData)),
            email: userData.dataValues.email
        }
    }
}

module.exports = new UserService();