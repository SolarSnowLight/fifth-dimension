module.exports = function (sequelize, DataTypes) {
    return sequelize.define('categories_subcategories', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        }
    });
};