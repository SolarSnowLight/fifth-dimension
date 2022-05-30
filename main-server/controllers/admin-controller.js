const ApiError = require('../exceptions/api-error');
const { validationResult } = require('express-validator');
const adminService = require('../services/admin/admin-service');
const AdminApiConstants = require('../constants/addresses/server/admin-api-constants');

class AdminController {
    async createCourse(req, res, next){
        try {
            await adminService.createCourse(JSON.parse(req.body.data), req.files);
            return res.status(201).json({
                created: true
            });
        }catch (e) {
            next(e);
        }
    }

    async updateCourse(req, res, next){
        try {
            await adminService.updateCourse(JSON.parse(req.body.data), req.files);
            return res.status(201).json({
                updated: true
            });
        }catch (e) {
            next(e);
        }
    }

    async archiveCourse(req, res, next){
        try {
            await adminService.archiveCourse(req.body);
            return res.status(201).json({
                archived: true
            });
        }catch (e) {
            next(e);
        }
    }

    async recoverCourse(req, res, next){
        try {
            await adminService.recoverCourse(req.body);
            return res.status(201).json({
                recovered: true
            });
        }catch (e) {
            next(e);
        }
    }

    async deleteCompletelyCourse(req, res, next){
        try {
            await adminService.deleteCompletelyCourse(req.body);
            return res.status(201).json({
                deleted: true
            });
        }catch (e) {
            next(e);
        }
    }
}

module.exports = new AdminController();