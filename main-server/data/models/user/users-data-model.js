module.exports = function (sequelize, DataTypes) {
    return sequelize.define('users_data', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true,
        },
        name: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        surname: {
            type: DataTypes.STRING,
            allowNull: false
        },
        date_register: {
            type: DataTypes.DATE,
            allowNull: false
        }
    }, {
        freezeTableName: true
    });
};