module.exports = function (sequelize, DataTypes) {
    return sequelize.define('payment', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },
        payment_id: {
            type: DataTypes.STRING(36),
            allowNull: false
        },
        idempotence_key: {
            type: DataTypes.STRING(36),
            allowNull: false
        },
        confirmation_token: {
            type: DataTypes.STRING(255),
            allowNull: false
        },
        payment_key: {
            type: DataTypes.STRING(36),
            allowNull: false
        },
        success_key: {
            type: DataTypes.STRING(36),
            allowNull: false
        },
        status: {
            type: DataTypes.STRING(100),
            allowNull: false
        },
    }, {
        freezeTableName: true
    });
};