module.exports = function (sequelize, DataTypes) {
    return sequelize.define('notifications', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        }
    });
};