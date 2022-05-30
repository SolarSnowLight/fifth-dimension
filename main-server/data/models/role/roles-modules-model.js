module.exports = function (sequelize, DataTypes) {
    return sequelize.define('roles_modules', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true,
        },
        user: {
            type: DataTypes.BOOLEAN,
            allowNull: false,
        },
        admin: {
            type: DataTypes.BOOLEAN,
            allowNull: false,
        },
        dev: {
            type: DataTypes.BOOLEAN,
            allowNull: false,
        }
    });
};