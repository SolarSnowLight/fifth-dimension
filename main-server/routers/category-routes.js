const Router = require('express');
const router = new Router();
const { check } = require('express-validator');
const CategoryApiConstants = require('../constants/addresses/server/category-api-constants');
const CategoryController = require('../controllers/category-controller');  
const authMiddleware = require('../middlewares/auth/auth-middleware');

// Маршрут: /category/get/all
router.post(
    CategoryApiConstants.get_categories,
    // authMiddleware,
    CategoryController.getAllCategories
);

// Route: /category/update/all
router.post(
    CategoryApiConstants.update_categories,
    authMiddleware,
    CategoryController.updateAllCategories
);

module.exports = router;