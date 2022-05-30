module.exports = function (sequelize, DataTypes) {
    return sequelize.define('courses_sounds', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },
        lesson_num: {
            type: DataTypes.INTEGER,
            allowNull: false
        }
    });
};