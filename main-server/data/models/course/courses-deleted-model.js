module.exports = function (sequelize, DataTypes) {
    return sequelize.define('courses_deleted', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        }
    }, {
        freezeTableName: true
    });
};