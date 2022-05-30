module.exports = function (sequelize, DataTypes) {
    return sequelize.define('activations', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true,
        },
        is_activated: {
            type: DataTypes.BOOLEAN,
            allowNull: false
        },
        activation_link: {
            type: DataTypes.STRING,
            allowNull: false
        }
    });
};