const { validationResult } = require('express-validator');
const categoryService = require('../services/category/category-service');
const ApiError = require('../exceptions/api-error');

class CategoryController {
    // Получение информации обо всех категориях
    async getAllCategories(req, res, next) {
        try {
            const data = await categoryService.getAllCategories();
            return res.status(201).json({
                categories: data
            });
        } catch (e) {
            next(e);
        }
    }

    async updateAllCategories(req, res, next){
        try {
            const { categories } = req.body;
            const data = await categoryService.updateAllCategories(categories, req.body.users_id);
            return res.status(201).json(data);
        } catch (e) {
            next(e);
        }
    }
}

module.exports = new CategoryController();