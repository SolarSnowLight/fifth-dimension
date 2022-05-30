module.exports = function (sequelize, DataTypes) {
    return sequelize.define('sounds', {
        id: {
            type: DataTypes.INTEGER,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },
        type: {
            type: DataTypes.STRING,
            allowNull: false
        },
        title: {
            type: DataTypes.STRING,
            allowNull: false
        },
        description: {
            type: DataTypes.STRING(512),
            allowNull: false
        },
        file_sound_name: {
            type: DataTypes.STRING,
            allowNull: false
        },
        file_sound_path: {
            type: DataTypes.STRING(1024),
            allowNull: false
        },
        subscribe: {
            type: DataTypes.BOOLEAN,
            allowNull: false
        },
        date_created: {
            type: DataTypes.DATE,
            allowNull: false
        },
        title_img_path:{
            type: DataTypes.STRING(1024),
            allowNull: false
        },
        title_img_name:{
            type: DataTypes.STRING,
            allowNull: false
        }
    });
};