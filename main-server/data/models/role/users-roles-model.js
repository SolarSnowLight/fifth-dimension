module.exports = function (sequelize, DataTypes) {
    return sequelize.define('users_roles', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true,
        }
    });
};