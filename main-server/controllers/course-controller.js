const ApiError = require('../exceptions/api-error');
const { validationResult } = require('express-validator');
const courseService = require('../services/course/course-service');
const CourseApiConstants = require('../constants/addresses/server/course-api-constants');

class CourseController {
    async getAllTitle(req, res, next) {
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для получения списка курсов по названию категории', errors.array())
                );
            }

            const { title } = req.body;

            const data = await courseService.getAllTitle(title, false, false);

            return res.status(201).json({
                courses: data
            });
        } catch (e) {
            next(e);
        }
    }

    async getAllDeletedTitle(req, res, next){
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для получения списка удалённых курсов по названию категории', errors.array())
                );
            }

            const { title } = req.body;

            const data = await courseService.getAllDeletedTitle(title);

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

    async getAllSounds(req, res, next){
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

    async getSoundFile(req, res, next){
        try {
            const errors = validationResult(req);

            if (!errors.isEmpty()) {
                return next(
                    ApiError.BadRequest('Некорректные данные для загрузки звукового файла конкретного урока', errors.array())
                );
            }

            const { sounds_id } = req.body;

            const data = await courseService.getSoundFile(sounds_id);

            return res.status(201)
                .set({
                    "filename": data.filename,
                    "sounds_id": sounds_id,
                    "subscribe": data.subscribe
                })
                .download(data.local_path, data.filename);
        } catch (e) {
            next(e);
        }
    }

    async getSoundTitleImg(req, res, next){
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

            const data = await courseService.getAllDate({
                day: refactorDate[2],
                month: refactorDate[1],
                year: refactorDate[0]
            });

            return res.status(201).json({
                courses: data
            });
        } catch (e) {
            next(e);
        }
    }
}

module.exports = new CourseController();