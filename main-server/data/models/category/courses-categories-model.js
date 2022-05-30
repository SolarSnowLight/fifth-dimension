module.exports = function (sequelize, DataTypes) {
    return sequelize.define('courses_categories', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },
        categories_subcategories_id: {
            type: DataTypes.INTEGER,
            allowNull: true
        }
    });
};