module.exports = function (sequelize, DataTypes) {
    return sequelize.define('subscribe', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },
        date_activation: {
            type: DataTypes.DATE,
            allowNull: false
        },
        date_completion: {
            type: DataTypes.DATE,
            allowNull: false
        }
    }, {
        freezeTableName: true
    });
};