module.exports = function (sequelize, DataTypes) {
    return sequelize.define('fixed_courses', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        }
    });
};