const Router = require('express');
const router = new Router();
const { check, body } = require('express-validator');
const multer = require("multer");
const fileUploader = multer({ dest: "data/files" });
const adminController = require('../controllers/admin-controller');
const AdminApiConstants = require('../constants/addresses/server/admin-api-constants');
const authMiddleware = require('../middlewares/auth/auth-middleware');

// Маршрут: /admin/create/course
router.post(
    AdminApiConstants.create_course,
    [
        authMiddleware,
        fileUploader.fields([{
            name: 'title_img'
        }, {
            name: 'title_img_sounds'
        }, {
            name: 'file_sounds'
        }])
    ],
    adminController.createCourse
);

// Маршрут: /admin/update/course
router.post(
    AdminApiConstants.update_course,
    [
        authMiddleware,
        fileUploader.fields([{
            name: 'title_img'
        }, {
            name: 'title_img_sounds'
        }, {
            name: 'file_sounds'
        }])
    ],
    adminController.updateCourse
);

// Маршрут: /admin/delete/course
router.post(
    AdminApiConstants.delete_course,
    [
        authMiddleware
    ],
    adminController.archiveCourse
);

// Маршрут: /admin/recover/course
router.post(
    AdminApiConstants.recover_course,
    [
        authMiddleware
    ],
    adminController.recoverCourse
);

// Маршрут: /admin/delete/completely/course
router.post(
    AdminApiConstants.delete_completely_course,
    [
        authMiddleware
    ],
    adminController.deleteCompletelyCourse
);

module.exports = router;