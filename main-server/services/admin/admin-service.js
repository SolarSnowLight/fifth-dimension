const roleService = require('../role/role-service');
const roleSecurityService = require('../role/role-security-service');
const userConverterService = require('../user/user-converter-service');
const AuthServiceValueConstants = require('../../constants/values/auth-service-value-constants');
const ApiError = require('../../exceptions/api-error');
const fs = require("fs");

const {
    CoursesModel,
    CoursesCategoriesModel,
    SoundsModel,
    CoursesSoundsModel,
    UsersModel,
    UsersRolesModel,
    RolesModel,
    RolesModulesModel,
    CoursesDeletedModel,
    CategoriesModel,
    SubcategoriesModel,
    CategoriesSubcategoriesModel,
} = require('../../data/index');
const CourseDto = require('../../dtos/course/course-dto');
const CourseInfoDto = require('../../dtos/course/course-info-dto');

class AdminService {
    findByOriginalName(id, values) {
        if((!values) || (!values.length)){
            return null;
        }

        for (let i = 0; i < values.length; i++) {
            if (values[i].originalname == id) {
                return values[i];
            }
        }

        return null;
    }

    async createCourse(data, files) {
        data.users_id = await userConverterService.convertUsersUuidToId(data.users_id);
        data.roles_id = await userConverterService.convertRolesUuidToId(data.roles_id);

        const access = await roleSecurityService.checkAccess(data.users_id, data.roles_id, 'admin');
        if (!access) {
            throw ApiError.Forbidden();
        }

        const categories = await CategoriesModel.findOne({
            where: {
                title: data.category
            }
        });

        if (!categories) {
            throw ApiError.BadRequest("Неопределённая категория");
        }

        const subcategories = (data.subcategory) ? (await CategoriesSubcategoriesModel.findOne({
            where: {
                subcategories_id: (await SubcategoriesModel.findOne({
                    where: {
                        title: data.subcategory
                    }
                })).id
            }
        })).id : null;

        const courseCreated = await CoursesModel.create({
            ...(new CourseDto(data)),
            date_created: new Date(),
            is_active: false,
            title_img_path: files.title_img[0].path,
            title_img_name: files.title_img[0].filename
        });

        await CoursesCategoriesModel.create({
            courses_id: courseCreated.id,
            categories_id: categories.id,
            categories_subcategories_id: subcategories
        });

        for (let i = 0; i < data.lessons.length; i++) {
            const item = data.lessons[i];
            const valueSound = this.findByOriginalName(String(item.num), files.file_sounds);
            const valueImgTitle = this.findByOriginalName(String(item.num), files.title_img_sounds);

            if (item) {
                const soundCreated = await SoundsModel.create({
                    users_id: data.users_id,
                    title: item.title,
                    type: item.type,
                    description: item.description,
                    subscribe: data.subscribe,
                    date_created: new Date(),
                    file_sound_name: valueSound.filename,
                    file_sound_path: valueSound.path,
                    title_img_name: (valueImgTitle) ? valueImgTitle.filename : files.title_img[0].filename,
                    title_img_path: (valueImgTitle) ? valueImgTitle.path : files.title_img[0].path,
                });

                await CoursesSoundsModel.create({
                    courses_id: courseCreated.id,
                    sounds_id: soundCreated.id,
                    lesson_num: item.num
                });
            }
        }
    }

    async updateCourse(data, files) {
        data.users_id = await userConverterService.convertUsersUuidToId(data.users_id);
        data.roles_id = await userConverterService.convertRolesUuidToId(data.roles_id);

        const access = await roleSecurityService.checkAccess(data.users_id, data.roles_id, 'admin');
        if (!access) {
            throw ApiError.Forbidden();
        }

        const course = await CoursesModel.findOne({
            where: {
                id: data.courses_id
            }
        });

        course.set({
            ...(new CourseInfoDto(data)),
            title_img_path: (files.title_img) ? files.title_img[0].path : course.dataValues.title_img_path,
            title_img_name: (files.title_img) ? files.title_img[0].filename : course.dataValues.title_img_name
        });

        await course.save();

        // Find update category
        const updateCategories = await CategoriesModel.findOne({
            where: {
                title: data.category
            }
        });

        const currentCategories = await CoursesCategoriesModel.findOne({
            where: {
                courses_id: course.dataValues.id
            }
        });

        if (currentCategories.categories_id !== updateCategories.dataValues.id) {
            // Update category for course
            currentCategories.set({
                categories_id: updateCategories.dataValues.id
            });
        }

        if (data.subcategory) {
            const subcategory = await SubcategoriesModel.findOne({
                where: {
                    title: data.subcategory
                }
            });

            const coursesSubcategories = await CategoriesSubcategoriesModel.findOne({
                where: {
                    categories_id: updateCategories.dataValues.id,
                    subcategories_id: subcategory.dataValues.id
                }
            });

            currentCategories.set({
                categories_subcategories_id: coursesSubcategories.id
            });
        } else {
            currentCategories.set({
                categories_subcategories_id: null
            });
        }

        await currentCategories.save();

        for (let i = 0; i < data.lessons.length; i++) {
            const item = data.lessons[i];
            const valueSound = this.findByOriginalName(String(item.id ?? item.num), files.file_sounds);
            const valueImgTitle = this.findByOriginalName(String(item.id ?? item.num), files.title_img_sounds);

            if (item) {
                const sound = (item.id) ? await SoundsModel.findOne({
                    where: {
                        id: item.id
                    }
                }) : null;

                if (sound) {
                    sound.set({
                        title: item.title,
                        type: item.type,
                        description: item.description,
                        file_sound_name: (valueSound)? valueSound.filename : sound.dataValues.file_sound_name,
                        file_sound_path: (valueSound)? valueSound.path : sound.dataValues.file_sound_path,
                        title_img_name: (valueImgTitle) ? valueImgTitle.filename : sound.dataValues.title_img_name,
                        title_img_path: (valueImgTitle) ? valueImgTitle.path : sound.dataValues.title_img_path,
                    });
                    
                    const courseSound = await CoursesSoundsModel.findOne({
                        where: {
                            sounds_id: item.id
                        }
                    });

                    courseSound.set({
                        lesson_num: item.num
                    });

                    await sound.save();
                    await courseSound.save();
                } else {
                    const soundCreated = await SoundsModel.create({
                        users_id: data.users_id,
                        title: item.title,
                        type: item.type,
                        description: item.description,
                        subscribe: data.subscribe,
                        date_created: new Date(),
                        file_sound_name: valueSound.filename,
                        file_sound_path: valueSound.path,
                        title_img_name: (valueImgTitle) ? valueImgTitle.filename : files.title_img[0].filename,
                        title_img_path: (valueImgTitle) ? valueImgTitle.path : files.title_img[0].path,
                    });

                    data.lessons[i].id = soundCreated.dataValues.id;

                    await CoursesSoundsModel.create({
                        courses_id: data.courses_id,
                        sounds_id: soundCreated.id,
                        lesson_num: item.num
                    });
                }
            }
        }

        const lessons = data.lessons;/*.sort((a, b) => {
            if(a.num > b.num){
                return 1;
            }else if(a.num < b.num){
                return -1;
            }else{
                return 0;
            }
        });*/

        const currentLessons = (await CoursesSoundsModel.findAll({
            where: {
                courses_id: data.courses_id
            }
        }));/*?.sort((a, b) => {
            if(a.lesson_num > b.lesson_num){
                return 1;
            }else if(a.lesson_num < b.lesson_num){
                return -1;
            }else{
                return 0;
            }
        });*/

        for(let i = 0; i < currentLessons.length; i++){
            const item = lessons.find((value) => {
                return (value.id === currentLessons[i].sounds_id);
            });

            if(!item){
                const delSound = currentLessons[i];
                const soundData = await SoundsModel.findOne({
                    where: {
                        id: delSound.id
                    }
                });

                const courseSoundData = await CoursesSoundsModel.findOne({
                    where: {
                        sounds_id: delSound.id
                    }
                });

                fs.unlinkSync(soundData.file_sound_path);
                fs.unlinkSync(soundData.title_img_path);

                await soundData.destroy();
                await courseSoundData.destroy();
            }
        }
    }

    async archiveCourse(data) {
        // data.users_id = await userConverterService.convertUsersUuidToId(data.users_id);
        // data.roles_id = await userConverterService.convertRolesUuidToId(data.roles_id);

        const access = await roleSecurityService.checkAccess(data.users_id, data.roles_id, 'admin');
        if (!access) {
            throw ApiError.Forbidden();
        }

        const courses = await CoursesModel.findOne({
            where: {
                id: data.id
            }
        });

        if(!courses){
            throw ApiError.BadRequest("Данного курса не существует!");
        }

        const archiveCourse = await CoursesDeletedModel.findOne({
            where: {
                courses_id: data.id
            }
        });

        if(archiveCourse){
            throw ApiError.BadRequest("Данный курс уже удалён!");
        }

        await CoursesDeletedModel.create({
            courses_id: data.id,
            users_id: data.users_id
        });
    }

    async recoverCourse(data) {
        // data.users_id = await userConverterService.convertUsersUuidToId(data.users_id);
        // data.roles_id = await userConverterService.convertRolesUuidToId(data.roles_id);

        const access = await roleSecurityService.checkAccess(data.users_id, data.roles_id, 'admin');
        if (!access) {
            throw ApiError.Forbidden();
        }

        const courses = await CoursesModel.findOne({
            where: {
                id: data.id
            }
        });

        if(!courses){
            throw ApiError.BadRequest("Данного курса не существует!");
        }

        const archiveCourse = await CoursesDeletedModel.findOne({
            where: {
                courses_id: data.id
            }
        });

        if(archiveCourse){
            await archiveCourse.destroy();
        }
    }

    async deleteCompletelyCourse(data) {
        // data.users_id = await userConverterService.convertUsersUuidToId(data.users_id);
        // data.roles_id = await userConverterService.convertRolesUuidToId(data.roles_id);

        const access = await roleSecurityService.checkAccess(data.users_id, data.roles_id, 'admin');
        if (!access) {
            throw ApiError.Forbidden();
        }

        const course = await CoursesModel.findOne({
            where: {
                id: data.id
            }
        });

        if(!course){
            throw ApiError.BadRequest("Данного курса не существует!");
        }

        const archiveCourse = await CoursesDeletedModel.findOne({
            where: {
                courses_id: data.id
            }
        });

        if(archiveCourse){
            await archiveCourse.destroy();
        }

        const currentCategories = await CoursesCategoriesModel.findOne({
            where: {
                courses_id: course.dataValues.id
            }
        });

        // Deleted connect course with category
        await currentCategories.destroy();

        const currentCoursesSounds = (await CoursesSoundsModel.findAll({
            where: {
                courses_id: course.dataValues.id
            }
        })).map((value) => {
            return {
                id: value.dataValues.id,
                sounds_id: value.dataValues.sounds_id
            }
        });

        for(let i = 0; i < currentCoursesSounds.length; i++){
            const courseSound = await CoursesSoundsModel.findOne({
                where: {
                    id: currentCoursesSounds[i].id
                }
            });

            // Delete connect course with sound
            await courseSound.destroy();

            const sound = await SoundsModel.findOne({
                where: {
                    id: currentCoursesSounds[i].sounds_id
                }
            });

            // Delete files of sound
            fs.unlinkSync(sound.file_sound_path);
            fs.unlinkSync(sound.title_img_path);

            // Delete record sounds
            await sound.destroy();
        }

        // Delete file of course
        fs.unlinkSync(course.title_img_path);

        // Delete completely course
        await course.destroy();
    }
}

module.exports = new AdminService();