const logger = require('./../logs/index');
const Sequelize = require('sequelize');
const usersDataModel = require('./models/user/users-data-model');
const coursesModel = require('./models/course/courses-model');

const sequelize = new Sequelize(
    process.env.POSTGRES_DB,
    process.env.POSTGRES_USER,
    process.env.POSTGRES_PASSWORD,
    {
        dialect: 'postgres',
        host: process.env.POSTGRES_HOST,
        port: process.env.POSTGRES_PORT,
        define: {
            timestamps: false
        },
        logging: false
    }
)

// Auth
const ActivationsModel = require('./models/auth/activations-model')(sequelize, Sequelize);
const TokensModel = require('./models/auth/tokens-model')(sequelize, Sequelize);
const TypeAuthModel = require('./models/auth/type-auth-model')(sequelize, Sequelize);

// Course
const CoursesModel = require('./models/course/courses-model')(sequelize, Sequelize);
const CoursesSoundsModel = require('./models/course/courses-sounds-model')(sequelize, Sequelize);
const SoundsModel = require('./models/course/sounds-model')(sequelize, Sequelize);
const FixedCoursesModel = require('./models/course/fixed-courses-model')(sequelize, Sequelize);
const CoursesDeletedModel = require("./models/course/courses-deleted-model")(sequelize, Sequelize);

// User
const UsersModel = require('./models/user/users-model')(sequelize, Sequelize);
const UsersDataModel = require('./models/user/users-data-model')(sequelize, Sequelize);
const UsersStatisticsModel = require('./models/user/users-statistics-model')(sequelize, Sequelize);

// Subscribe
const SubscribeModel = require('./models/subscribe/subscribe-model')(sequelize, Sequelize);
const TariffModel = require('./models/subscribe/tariff-model')(sequelize, Sequelize);

// Role
const RolesModel = require('./models/role/roles-model')(sequelize, Sequelize);
const UsersRolesModel = require('./models/role/users-roles-model')(sequelize, Sequelize);
const RolesModulesModel = require('./models/role/roles-modules-model')(sequelize, Sequelize);

// Category
const CategoriesModel = require('./models/category/categories-model')(sequelize, Sequelize);
const CategoriesSubcategoriesModel = require('./models/category/categories-subcategories-model')(sequelize, Sequelize);
const CoursesCategoriesModel = require('./models/category/courses-categories-model')(sequelize, Sequelize);
const SubcategoriesModel = require('./models/category/subcategories-model')(sequelize, Sequelize);

// Внешний ключ на таблицу users
const foreignkKeyUsersNN = {
    foreignKey: {
        name: 'users_id',
        allowNull: false,
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
    }
};

const foreignkKeyUsers = {
    foreignKey: {
        name: 'users_id',
        allowNull: true,
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
    }
};

// Внешний ключ на таблицу roles
const foreignkKeyRolesNN = {
    foreignKey: {
        name: 'roles_id',
        allowNull: false,
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
    }
};

// Внешний ключ на таблицу tariff
const foreignkKeyTariffNN = {
    foreignKey: {
        name: 'tariff_id',
        allowNull: false,
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
    }
};

// Внешний ключ на таблицу courses
const foreignkKeyCoursesNN = {
    foreignKey: {
        name: 'courses_id',
        allowNull: false,
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
    }
};

// Внешний ключ на таблицу sounds
const foreignkKeySoundsNN = {
    foreignKey: {
        name: 'sounds_id',
        allowNull: false,
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
    }
};

// Внешний ключ на таблицу categories
const foreignkKeyCategoriesNN = {
    foreignKey: {
        name: 'categories_id',
        allowNull: false,
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
    }
};

// Внешний ключ на таблицу subcategories
const foreignkKeySubcategoriesNN = {
    foreignKey: {
        name: 'subcategories_id',
        allowNull: false,
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
    }
};

// Внешний ключ на таблицу courses_sounds
const foreignkKeyCoursesSoundsNN = {
    foreignKey: {
        name: 'courses_sounds_id',
        allowNull: false,
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
    }
};

UsersModel.hasMany(CategoriesModel, foreignkKeyUsersNN);
CategoriesModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(UsersStatisticsModel, foreignkKeyUsersNN);
UsersStatisticsModel.belongsTo(UsersModel, foreignkKeyUsersNN);

CoursesSoundsModel.hasMany(UsersStatisticsModel, foreignkKeyCoursesSoundsNN);
UsersStatisticsModel.belongsTo(CoursesSoundsModel, foreignkKeyCoursesSoundsNN);

UsersModel.hasMany(CoursesDeletedModel, foreignkKeyUsersNN);
CoursesDeletedModel.belongsTo(UsersModel, foreignkKeyUsersNN);

CoursesModel.hasMany(CoursesDeletedModel, foreignkKeyCoursesNN);
CoursesDeletedModel.belongsTo(CoursesModel, foreignkKeyCoursesNN);

UsersModel.hasMany(SubcategoriesModel, foreignkKeyUsersNN);
SubcategoriesModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(TypeAuthModel, foreignkKeyUsersNN);
TypeAuthModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(CoursesModel, foreignkKeyUsersNN);
CoursesModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(SoundsModel, foreignkKeyUsersNN);
SoundsModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(FixedCoursesModel, foreignkKeyUsersNN);
FixedCoursesModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(TokensModel, foreignkKeyUsersNN);
TokensModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(UsersDataModel, foreignkKeyUsersNN);
UsersDataModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(ActivationsModel, foreignkKeyUsersNN);
ActivationsModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(SubscribeModel, foreignkKeyUsersNN);
SubscribeModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(TariffModel, foreignkKeyUsersNN);
TariffModel.belongsTo(UsersModel, foreignkKeyUsersNN);

UsersModel.hasMany(RolesModel, foreignkKeyUsers);
RolesModel.belongsTo(UsersModel, foreignkKeyUsers);

UsersModel.hasMany(UsersRolesModel, foreignkKeyUsersNN);
UsersRolesModel.belongsTo(UsersModel, foreignkKeyUsersNN);

RolesModel.hasMany(UsersRolesModel, foreignkKeyRolesNN);
UsersRolesModel.belongsTo(RolesModel, foreignkKeyRolesNN);

TariffModel.hasMany(SubscribeModel, foreignkKeyTariffNN);
SubscribeModel.belongsTo(TariffModel, foreignkKeyTariffNN);

RolesModel.hasMany(RolesModulesModel, foreignkKeyRolesNN);
RolesModulesModel.belongsTo(RolesModel, foreignkKeyRolesNN);

CoursesModel.hasMany(CoursesSoundsModel, foreignkKeyCoursesNN);
CoursesSoundsModel.belongsTo(CoursesModel, foreignkKeyCoursesNN);

SoundsModel.hasMany(CoursesSoundsModel, foreignkKeySoundsNN);
CoursesSoundsModel.belongsTo(SoundsModel, foreignkKeySoundsNN);

CoursesModel.hasMany(FixedCoursesModel, foreignkKeyCoursesNN);
FixedCoursesModel.belongsTo(CoursesModel, foreignkKeyCoursesNN);

// Courses categories
CoursesModel.hasMany(CoursesCategoriesModel, foreignkKeyCoursesNN);
CoursesCategoriesModel.belongsTo(CoursesModel, foreignkKeyCoursesNN);

CategoriesModel.hasMany(CoursesCategoriesModel, foreignkKeyCategoriesNN);
CoursesCategoriesModel.belongsTo(CategoriesModel, foreignkKeyCategoriesNN);

// Categories subcategories
CategoriesModel.hasMany(CategoriesSubcategoriesModel, foreignkKeyCategoriesNN);
CategoriesSubcategoriesModel.belongsTo(CategoriesModel, foreignkKeyCategoriesNN);

SubcategoriesModel.hasMany(CategoriesSubcategoriesModel, foreignkKeySubcategoriesNN);
CategoriesSubcategoriesModel.belongsTo(SubcategoriesModel, foreignkKeySubcategoriesNN);

sequelize.sync().then(res => {
    logger.info({
        message: "Синхронизация моделей с базой данных",
        result: res
    });
}).catch(error => {
    console.log(error);
    logger.error({
        message: "Ошибка при синхронизации моделей с базой данных",
        error: error
    });
});

// Auth
module.exports.TypeAuthModel = TypeAuthModel;
module.exports.TokensModel = TokensModel;
module.exports.ActivationsModel = ActivationsModel;

// User
module.exports.UsersModel = UsersModel;
module.exports.UsersDataModel = UsersDataModel;
module.exports.UsersStatisticsModel = UsersStatisticsModel;

// Subscribe
module.exports.SubscribeModel = SubscribeModel;
module.exports.TariffModel = TariffModel;

// Role
module.exports.UsersRolesModel = UsersRolesModel;
module.exports.RolesModel = RolesModel;
module.exports.RolesModulesModel = RolesModulesModel;

// Course
module.exports.CoursesModel = CoursesModel;
module.exports.CoursesSoundsModel = CoursesSoundsModel;
module.exports.FixedCoursesModel = FixedCoursesModel;
module.exports.SoundsModel = SoundsModel;
module.exports.CoursesDeletedModel = CoursesDeletedModel;

// Category
module.exports.CategoriesModel = CategoriesModel;
module.exports.SubcategoriesModel = SubcategoriesModel;
module.exports.CoursesCategoriesModel = CoursesCategoriesModel;
module.exports.CategoriesSubcategoriesModel = CategoriesSubcategoriesModel;

// Sequelize
module.exports.sequelize = sequelize;
module.exports.Sequelize = Sequelize;