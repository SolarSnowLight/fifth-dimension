const ApiError = require('../../exceptions/api-error');
const {
    CategoriesModel, SubcategoriesModel,
    CategoriesSubcategoriesModel,
    CoursesModel, CoursesCategoriesModel,
    CoursesSoundsModel, SoundsModel,
    UsersStatisticsModel,
    TariffModel, SubscribeModel,
    CoursesDeletedModel, Sequelize
} = require('../../data/index');
const CourseDto = require("../../dtos/course/course-dto");
const SoundDto = require("../../dtos/sound/sound-dto");

class CourseService {
    async getAllTitle(title, isOpen = false, isActive = true) {
        const category = await CategoriesModel.findOne({
            where: {
                title: title
            }
        });

        if (!category) {
            throw ApiError.BadRequest("Категории с данным названием не существует");
        }

        const coursesIds = await CoursesCategoriesModel.findAll({
            where: {
                categories_id: category.id
            }
        });

        const courses = [];
        for (let i = 0; i < coursesIds.length; i++) {
            const index = coursesIds[i];
            const data = (isActive) ?
                await CoursesModel.findOne({
                    where: {
                        id: index.courses_id,
                        is_active: true
                    }
                })
                :
                await CoursesModel.findOne({
                    where: {
                        id: index.courses_id
                    }
                });

            const deletedCourse = await CoursesDeletedModel.findOne({
                where: {
                    courses_id: index.courses_id
                }
            });

            if ((!data)
                || (deletedCourse)
                || (isOpen && ((new Date()) > (new Date(data.dataValues.date_open))))) {
                continue;
            }

            // Список уроков, которые доступны данному курсу
            const sounds = await CoursesSoundsModel.findAll({
                where: {
                    courses_id: data.dataValues.id
                }
            });

            for (let i = 0; i < sounds.length; i++) {
                const soundInfo = await SoundsModel.findOne({
                    where: {
                        id: sounds[i].sounds_id
                    }
                });

                sounds[i].dataValues.title_img_path = soundInfo.dataValues.title_img_path;
                sounds[i].dataValues.file_sound_path = soundInfo.dataValues.file_sound_path;
                sounds[i].dataValues.title = soundInfo.dataValues.title;
                sounds[i].dataValues.description = soundInfo.dataValues.description;
                sounds[i].dataValues.type = soundInfo.dataValues.type;
            }

            // Категория, к которой принадлежит курс
            const category = await CoursesCategoriesModel.findOne({
                where: {
                    courses_id: data.dataValues.id
                }
            });

            const categoryTitle = (await CategoriesModel.findOne({
                where: {
                    id: category.categories_id
                }
            }))?.title ?? null;

            // Подкатегория, к которой принадлежит курс
            const subcategory = (category.dataValues.categories_subcategories_id) ?
                (await CategoriesSubcategoriesModel.findOne({
                    where: {
                        id: category.dataValues.categories_subcategories_id
                    }
                }))?.subcategories_id ?? null
                : null;

            const subcategoryTitle = (subcategory) ? (await SubcategoriesModel.findOne({
                where: {
                    id: subcategory
                }
            }))?.title ?? null
                : null;

            courses.push({
                id: data.dataValues.id,
                ...(new CourseDto(data.dataValues)),
                users_id: undefined,
                category: category.dataValues.categories_id,
                category_title: categoryTitle,
                subcategory: subcategory,
                subcategory_title: subcategoryTitle,
                sounds: sounds.map((item) => {
                    return {
                        id: item.dataValues.sounds_id,
                        description: item.dataValues.description,
                        lesson_num: item.dataValues.lesson_num,
                        title_img_path: item.dataValues.title_img_path,
                        file_sound_path: item.dataValues.file_sound_path,
                        title: item.dataValues.title,
                        type: item.dataValues.type
                    };
                }),
                title_img_path: data.dataValues.title_img_path
            });
        }

        return courses;
    }

    async getAllDeletedTitle(title) {
        const category = await CategoriesModel.findOne({
            where: {
                title: title
            }
        });

        if (!category) {
            throw ApiError.BadRequest("Категории с данным названием не существует");
        }

        const coursesIds = await CoursesCategoriesModel.findAll({
            where: {
                categories_id: category.id
            }
        });

        const courses = [];
        for (let i = 0; i < coursesIds.length; i++) {
            const index = coursesIds[i];
            const data = await CoursesModel.findOne({
                where: {
                    id: index.courses_id,
                    is_active: true
                }
            });

            const deletedCourse = await CoursesDeletedModel.findOne({
                where: {
                    courses_id: index.courses_id
                }
            });

            if ((data) && (deletedCourse)) {
                const sounds = await CoursesSoundsModel.findAll({
                    where: {
                        courses_id: data.dataValues.id
                    }
                });

                for (let i = 0; i < sounds.length; i++) {
                    const soundInfo = await SoundsModel.findOne({
                        where: {
                            id: sounds[i].sounds_id
                        }
                    });

                    sounds[i].dataValues.title_img_path = soundInfo.dataValues.title_img_path;
                    sounds[i].dataValues.file_sound_path = soundInfo.dataValues.file_sound_path;
                    sounds[i].dataValues.title = soundInfo.dataValues.title;
                    sounds[i].dataValues.description = soundInfo.dataValues.description;
                    sounds[i].dataValues.type = soundInfo.dataValues.type;
                }

                // Категория, к которой принадлежит курс
                const category = await CoursesCategoriesModel.findOne({
                    where: {
                        courses_id: data.dataValues.id
                    }
                });

                const categoryTitle = (await CategoriesModel.findOne({
                    where: {
                        id: category.categories_id
                    }
                }))?.title ?? null;

                // Подкатегория, к которой принадлежит курс
                const subcategory = (category.dataValues.categories_subcategories_id) ?
                    (await CategoriesSubcategoriesModel.findOne({
                        where: {
                            id: category.dataValues.categories_subcategories_id
                        }
                    }))?.subcategories_id ?? null
                    : null;

                const subcategoryTitle = (subcategory) ? (await SubcategoriesModel.findOne({
                    where: {
                        id: subcategory
                    }
                }))?.title ?? null
                    : null;

                courses.push({
                    id: data.dataValues.id,
                    ...(new CourseDto(data.dataValues)),
                    users_id: undefined,
                    category: category.dataValues.categories_id,
                    category_title: categoryTitle,
                    subcategory: subcategory,
                    subcategory_title: subcategoryTitle,
                    sounds: sounds.map((item) => {
                        return {
                            id: item.dataValues.sounds_id,
                            description: item.dataValues.description,
                            lesson_num: item.dataValues.lesson_num,
                            title_img_path: item.dataValues.title_img_path,
                            file_sound_path: item.dataValues.file_sound_path,
                            title: item.dataValues.title,
                            type: item.dataValues.type
                        };
                    }),
                    title_img_path: data.dataValues.title_img_path
                });
            }
        }

        return courses;
    }

    async getAllSounds(coursesId) {
        const courseSounds = await CoursesSoundsModel.findAll({
            where: {
                courses_id: coursesId
            }
        });

        const result = [];
        for (let i = 0; i < courseSounds.length; i++) {
            const item = await SoundsModel.findOne({
                where: {
                    id: courseSounds[i].sounds_id
                }
            });

            if (item) {
                result.push({
                    id: courseSounds[i].dataValues.sounds_id,
                    ...(new SoundDto(item.dataValues)),
                    lesson_num: courseSounds[i].dataValues.lesson_num,
                    description: item.dataValues.description,
                    title_img_path: item.dataValues.title_img_path
                });
            }
        }

        return result;
    }

    async getAllSoundsAvailable(usersId, coursesId, soundsId) {
        const sounds = (await CoursesSoundsModel.findAll({
            where: {
                courses_id: coursesId
            },
            include: {
                model: UsersStatisticsModel,
                required: false,
                where: {
                    users_id: usersId
                }
            }
        })).map((value) => {
            return value.dataValues;
        });

        const firstSound = sounds.find((value) => {
            return (value.lesson_num === 1);
        });

        if ((firstSound.courses_id === coursesId) && (firstSound.sounds_id === soundsId)) {
            let firstSoundData = await UsersStatisticsModel.findOne({
                where: {
                    courses_sounds_id: firstSound.id
                }
            });

            if (!firstSoundData) {
                firstSoundData = await UsersStatisticsModel.create({
                    users_id: usersId,
                    courses_sounds_id: firstSound.id,
                    completed: false
                });
            }

            const sounds = await SoundsModel.findOne({
                where: {
                    id: soundsId
                }
            });

            if (!sounds) {
                throw ApiError.BadRequest("Звукового файла для ресурса не найдено");
            }

            return {
                filename: sounds.file_sound_name,
                local_path: sounds.file_sound_path,
                subscribe: sounds.subscribe
            };
        }

        sounds.sort((a, b) => {
            if (a.lesson_num > b.lesson_num) {
                return 1;
            } else if (a.lesson_num < b.lesson_num) {
                return (-1);
            } else {
                return 0;
            }
        });

        const findSound = sounds.find((value) => {
            return (value.courses_id === coursesId) && (value.sounds_id === soundsId);
        });

        if (findSound.users_statistics.length > 0) {
            const sounds = await SoundsModel.findOne({
                where: {
                    id: findSound.sounds_id
                }
            });

            if (!sounds) {
                throw ApiError.BadRequest("Звукового файла для ресурса не найдено");
            }

            return {
                filename: sounds.file_sound_name,
                local_path: sounds.file_sound_path,
                subscribe: sounds.subscribe
            };
        }

        return null;
    }

    async getTitleImg(coursesId) {
        const course = await CoursesModel.findOne({
            where: {
                id: coursesId
            }
        });

        if (!course) {
            throw ApiError.BadRequest("Изображения для курса не найдено!");
        }

        return {
            filename: course.title_img_name,
            local_path: course.title_img_path
        };
    }

    async getSoundFile(soundsId) {
        const sounds = await SoundsModel.findOne({
            where: {
                id: soundsId
            }
        });

        if (!sounds) {
            throw ApiError.BadRequest("Звукового файла для ресурса не найдено");
        }

        return {
            filename: sounds.file_sound_name,
            local_path: sounds.file_sound_path,
            subscribe: sounds.subscribe
        };
    }

    async getSoundTitleImg(soundsId) {
        const sounds = await SoundsModel.findOne({
            where: {
                id: soundsId
            }
        });

        if (!sounds) {
            throw ApiError.BadRequest("Изображения для ресурса не найдено");
        }

        return {
            filename: sounds.title_img_name,
            local_path: sounds.title_img_path
        };
    }

    async getAllDate(date, isActive = true) {
        let courses = await CoursesModel.findAll();
        courses = courses.filter((value) => {
            const currentDate = new Date(value.date_open);

            const refactorDate = currentDate.toISOString().slice(0, 10).split('-').map((value) => {
                return Number(value);
            });

            return ((isActive) ?
                (
                    (refactorDate[2] === date.day)
                    && (refactorDate[1] == date.month)
                    && (refactorDate[0] == date.year)
                    && (value.is_active == true)
                )
                :
                (
                    (refactorDate[2] === date.day)
                    && (refactorDate[1] == date.month)
                    && (refactorDate[0] == date.year)
                ));
        });

        const results = [];
        for (let i = 0; i < courses.length; i++) {
            const data = courses[i];

            if (data) {
                const sounds = await CoursesSoundsModel.findAll({
                    where: {
                        courses_id: data.dataValues.id
                    }
                });

                const category = await CoursesCategoriesModel.findOne({
                    where: {
                        courses_id: data.dataValues.id
                    }
                });

                const subcategory = (category.dataValues.categories_subcategories_id) ?
                    (await CategoriesSubcategoriesModel.findOne({
                        where: {
                            id: category.dataValues.categories_subcategories_id
                        }
                    })).subcategories_id
                    : null;

                results.push({
                    id: data.dataValues.id,
                    ...(new CourseDto(data.dataValues)),
                    users_id: undefined,
                    category: category.dataValues.categories_id,
                    subcategory: subcategory,
                    title_img_path: data.dataValues.title_img_path,
                    sounds: sounds.map((item) => {
                        return {
                            id: item.dataValues.id,
                            lesson_num: item.dataValues.lesson_num,
                            title_img_path: item.dataValues.title_img_path,
                            type: item.dataValues.type
                        };
                    })
                });
            }
        }

        return results;
    }

    async setCompletetionSound(usersId, coursesId, soundsId) {
        const sounds = (await CoursesSoundsModel.findAll({
            where: {
                courses_id: coursesId
            },
            include: {
                model: UsersStatisticsModel,
                required: false,
                where: {
                    users_id: usersId
                }
            }
        })).map((value) => {
            return value.dataValues;
        });

        sounds.sort((a, b) => {
            if (a.lesson_num > b.lesson_num) {
                return 1;
            } else if (a.lesson_num < b.lesson_num) {
                return (-1);
            } else {
                return 0;
            }
        });

        const findSound = sounds.find((value) => {
            return (value.courses_id === coursesId) && (value.sounds_id === soundsId);
        });

        if (findSound.users_statistics.length <= 0) {
            return false;
        }

        if (findSound.users_statistics[0].completed) {
            return false;
        }

        findSound.users_statistics[0].set({
            completed: true
        });

        await findSound.users_statistics[0].save();

        const nextFindSound = sounds.find((value) => {
            return (value.lesson_num == (findSound.lesson_num + 1));
        });

        if (nextFindSound) {
            if (nextFindSound.users_statistics.length > 0) {
                return false;
            }

            await UsersStatisticsModel.create({
                users_id: usersId,
                courses_sounds_id: nextFindSound.id,
                completed: false
            });
        }

        return true;
    }

    async getNewCourses() {
        let courses = await CoursesModel.findAll({
            where: {
                is_active: true
            }
        });

        if (!courses) {
            return null;
        }

        const [year, month, day] = (new Date()).toISOString().slice(0, 10).split('-');

        courses = courses.filter((value) => {
            const currentDate = new Date(value.date_open);

            const refactorDate = currentDate.toISOString().slice(0, 10).split('-').map((value) => {
                return Number(value);
            });

            return ((refactorDate[2] === Number(day))
                && (refactorDate[1] == Number(month))
                && (refactorDate[0] == Number(year)));
        });

        const result = [];
        for (let i = 0; i < courses.length; i++) {
            const courseCategory = await CoursesCategoriesModel.findOne({
                where: {
                    courses_id: courses[i].id
                }
            });

            if (!courseCategory) {
                continue;
            }

            const category = await CategoriesModel.findOne({
                where: {
                    id: courseCategory.dataValues.categories_id
                }
            });

            if (category) {
                result.push({
                    ...courses[i].dataValues,
                    categoryTitle: category.title
                });
            }
        }

        return result;
    }

    // Updating the status of courses
    async CourseUpdateStatus() {
        const noActiveCourses = await CoursesModel.findAll({
            where: {
                is_active: false
            }
        });

        const dateNow = new Date((new Date()).toISOString().slice(0, 10));

        for (let i = 0; i < noActiveCourses.length; i++) {
            const course = noActiveCourses[i].dataValues;

            if ((new Date(course.date_open.toISOString().slice(0, 10))) <= dateNow) {
                noActiveCourses[i].set({
                    is_active: true
                });

                await noActiveCourses[i].save();
            }
        }
    }

    // Check subscribe for user
    async checkSubscribe(usersId) {
        const userSubscribe = await SubscribeModel.findOne({
            where: {
                users_id: usersId
            }
        });

        if(!userSubscribe){
            return false;
        }

        const dateNow = new Date((new Date()).toISOString().slice(0, 10));
        const dateCompletetion = new Date(userSubscribe.date_completion.toISOString().slice(0, 10));

        if(dateNow > dateCompletetion){
            return false;
        }

        return true;
    }

    // Check subscribe for course
    async checkSubscribeCourse(coursesId) {
        const course = await CoursesModel.findOne({
            where: {
                id: coursesId   
            }
        });

        if(!course){
            return false;
        }

        return course.dataValues.subscribe;
    }
}

module.exports = new CourseService();