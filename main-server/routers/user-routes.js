const Router = require('express');
const router = new Router();
const { check, body } = require('express-validator');
const userController = require('../controllers/user-controller');
const UserApiConstants = require('../constants/addresses/server/user-api-constants');
const authMiddleware = require('../middlewares/auth/auth-middleware');

// Маршрут: /user/info
router.post(
    UserApiConstants.get_info,
    authMiddleware,
    userController.getUserInfo
);

// Маршрут: /user/get/all/title
router.post(
    UserApiConstants.get_all_title,
    [
        // authMiddleware,
        check('title', 'Минимальная длина названия категории равна одному символу')
            .isLength({ min: 1 }),
    ],
    userController.getAllTitle
);

// Маршрут: /user/get/title/img
router.post(
    UserApiConstants.get_title_img,
    [
        // authMiddleware,
        check('courses_id', 'Идентификатор курса должен быть числом').isNumeric()
    ],
    userController.getTitleImg
);

// Маршрут: /user/get/all/sounds
router.post(
    UserApiConstants.get_all_sounds,
    [
        //authMiddleware,
        check('courses_id', 'Идентификатор курса должен быть числом').isNumeric()
    ],
    userController.getAllSounds
);

// Маршрут: /user/get/sound
router.post(
    UserApiConstants.get_sound,
    [
        // authMiddleware,
        check('sounds_id', 'Идентификатор звукового файла должен быть числом').isNumeric()
    ],
    userController.getSoundFile
);

// Маршрут: /user/get/title/img/sound
router.post(
    UserApiConstants.get_title_img_sound,
    [
        //authMiddleware,
        check('sounds_id', 'Идентификатор звукового файла должен быть числом').isNumeric()
    ],
    userController.getSoundTitleImg
);

// Маршрут: /user/get/all/date
router.post(
    UserApiConstants.get_all_date,
    [
        //authMiddleware,
        check('date', 'Дата представлена в некорректном формате').isDate({ format: 'YYYY-MM-DD' })
    ],
    userController.getAllDate
);

// Маршрут: /user/complete/sound
router.post(
    UserApiConstants.completetion_sound,
    [
        authMiddleware,
    ],
    userController.setCompletetionSound
);

// Маршрут: /user/get/new/courses
router.post(
    UserApiConstants.get_new_courses,
    [
        //authMiddleware,
    ],
    userController.getNewCourses
);

// Route: /user/check/subscribe
router.post(
    UserApiConstants.check_subscribe,
    [
        authMiddleware
    ],
    userController.checkSubscribe
);

// Route: /user/info/update
router.post(
    UserApiConstants.update_info,
    [
        authMiddleware,
        check('name', 'Минимальная длина имени равна трём символам')
            .isLength({ min: 3 }),
        check('surname', 'Минимальная длина фамилии равна трём символам')
            .isLength({ min: 3 })
    ],
    userController.updateUserInfo
)

module.exports = router;