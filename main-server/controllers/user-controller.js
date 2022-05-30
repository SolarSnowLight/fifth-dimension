const { validationResult } = require('express-validator');
const userService = require('../services/user/user-service');
const courseService = require('../services/course/course-service');
const UserApiConstants = require('../constants/addresses/server/user-api-constants');
const ApiError = require('../exceptions/api-error');

class UserController {
    async getUserInfo(req, res, next) {
        try {
            const { users_id } = req.body;
            const data = await userService.getUserInfo(users_id);

            return res.status(201).json(data);
        } catch (e) {
            next(e);
        }
    }

    async getAllTitle(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для получения списка курсов по названию категории', errors.array())
                );
            }

            const { title } = req.body;

            const data = await courseService.getAllTitle(title);

            return res.status(201).json({
                courses: data
            });
        } catch (e) {
            next(e);
        }
    }

    async getTitleImg(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для загрузки изображения конкретного курса', errors.array())
                );
            }

            const { courses_id } = req.body;

            const data = await courseService.getTitleImg(courses_id);

            return res.status(201)
                .set({
                    "filename": data.filename,
                    "courses_id": courses_id
                })
                .download(data.local_path, data.filename);
        } catch (e) {
            next(e);
        }
    }

    async getAllSounds(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для загрузки списка уроков конкретного курса', errors.array())
                );
            }

            const { courses_id } = req.body;

            const data = await courseService.getAllSounds(courses_id);

            return res.status(201).json({
                sounds: data
            });
        } catch (e) {
            next(e);
        }
    }

    async getSoundFile(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для загрузки звукового файла конкретного урока', errors.array())
                );
            }

            const { users_id, courses_id, sounds_id } = req.body;

            let data = null;

            if ((await courseService.checkSubscribeCourse(courses_id))) {
                const subscribe = await courseService.checkSubscribe(users_id);
                if (subscribe) {
                    data = await courseService.getAllSoundsAvailable(users_id, courses_id, sounds_id);
                } else {
                    return res.status(204).set({
                        "sounds_id": sounds_id
                    })
                        .json({
                            sounds_id: sounds_id
                        });
                }
            } else {
                data = await courseService.getAllSoundsAvailable(users_id, courses_id, sounds_id);
            }

            if (data) {
                return res.status(201)
                    .set({
                        "filename": data.filename,
                        "sounds_id": sounds_id,
                        "subscribe": data.subscribe
                    })
                    .download(data.local_path, data.filename);
            } else {
                return res.status(204).set({
                    "sounds_id": sounds_id
                })
                    .json({
                        sounds_id: sounds_id
                    });
            }
        } catch (e) {
            next(e);
        }
    }

    async getSoundTitleImg(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для загрузки изображения конкретного урока', errors.array())
                );
            }

            const { sounds_id } = req.body;

            const data = await courseService.getSoundTitleImg(sounds_id);

            return res.status(201)
                .set({
                    "filename": data.filename,
                    "sounds_id": sounds_id
                })
                .download(data.local_path, data.filename);
        } catch (e) {
            next(e);
        }
    }

    async getAllDate(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для получения списка курсов по определённой дате', errors.array())
                );
            }

            const { date } = req.body;
            const refactorDate = date.split('-').map((value) => {
                return Number(value);
            });

            const data = await courseService.getAllDate(
                {
                    day: refactorDate[2],
                    month: refactorDate[1],
                    year: refactorDate[0]
                },
                false
            );

            return res.status(201).json({
                courses: data
            });
        } catch (e) {
            next(e);
        }
    }

    async setCompletetionSound(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для прохождения урока', errors.array())
                );
            }

            const { users_id, courses_id, sounds_id } = req.body;

            const data = await courseService.setCompletetionSound(users_id, courses_id, sounds_id);

            return res.status(201).json({
                completed: data
            });
        } catch (e) {
            next(e);
        }
    }

    async getNewCourses(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для получения новых курсов', errors.array())
                );
            }

            const data = await courseService.getNewCourses();

            return res.status(201).json({
                courses: data
            });
        } catch (e) {
            next(e);
        }
    }

    async checkSubscribe(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для получения информации о подписке', errors.array())
                );
            }

            const { users_id } = req.body;

            if (!users_id) {
                return res.status(201).json({
                    subscribe: false
                });
            }

            const data = await courseService.checkSubscribe(users_id);

            return res.status(201).json({
                subscribe: data
            });
        } catch (e) {
            next(e);
        }
    }

    async updateUserInfo(req, res, next){
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для обновления пользовательских данных', errors.array())
                );
            }

            const { users_id, name, surname, password } = req.body;

            const data = await userService.updateUserInfo(users_id, name, surname, password);

            return res.status(201).json(data);
        } catch (e) {
            next(e);
        }
    }
}

module.exports = new UserController();