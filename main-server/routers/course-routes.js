const Router = require('express');
const router = new Router();
const { check, body } = require('express-validator');
const courseController = require('../controllers/course-controller');
const CourseApiConstants = require('../constants/addresses/server/course-api-constants');
const authMiddleware = require('../middlewares/auth/auth-middleware');

// Маршрут: /course/get/all/title
router.post(
    CourseApiConstants.get_all_title,
    [
        // authMiddleware,
        check('title', 'Минимальная длина названия категории равна одному символу')
            .isLength({ min: 1 }),
    ],
    courseController.getAllTitle
);

// Маршрут: /course/get/all/title/deleted
router.post(
    CourseApiConstants.get_all_title_deleted,
    [
        authMiddleware,
        check('title', 'Минимальная длина названия категории равна одному символу')
            .isLength({ min: 1 }),
    ],
    courseController.getAllDeletedTitle
);

// Маршрут: /course/get/title/img
router.post(
    CourseApiConstants.get_title_img,
    [
        // authMiddleware,
        check('courses_id', 'Идентификатор курса должен быть числом').isNumeric()
    ],
    courseController.getTitleImg
);

// Маршрут: /course/get/all/sounds
router.post(
    CourseApiConstants.get_all_sounds,
    [
        //authMiddleware,
        check('courses_id', 'Идентификатор курса должен быть числом').isNumeric()
    ],
    courseController.getAllSounds
);

// Маршрут: /course/get/sound
router.post(
    CourseApiConstants.get_sound,
    [
        //authMiddleware,
        check('sounds_id', 'Идентификатор звукового файла должен быть числом').isNumeric()
    ],
    courseController.getSoundFile
);

// Маршрут: /course/get/title/img/sound
router.post(
    CourseApiConstants.get_title_img_sound,
    [
        //authMiddleware,
        check('sounds_id', 'Идентификатор звукового файла должен быть числом').isNumeric()
    ],
    courseController.getSoundTitleImg
);

// Маршрут: /course/get/all/date
router.post(
    CourseApiConstants.get_all_date,
    [
        //authMiddleware,
        check('date', 'Дата представлена в некорректном формате').isDate({format: 'YYYY-MM-DD'})
    ],
    courseController.getAllDate
);

module.exports = router;