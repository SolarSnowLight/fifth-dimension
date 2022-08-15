const ApiError = require('../exceptions/api-error');
const { validationResult } = require('express-validator');
const paymentService = require('../services/payment/payment-service');
const PaymentApiConstants = require('../constants/addresses/server/payment-api-constants');

class PaymentController {
    async postTarrifGet(req, res, next){
        try {
            const data = await paymentService.tariffGet();
            
            return res.status(201).json({
                tariff: data
            });
        }catch (e) {
            next(e);
        }
    }

    async postCreatePayment(req, res, next){
        try {
            const { users_id, tariff_id } = req.body;

            const link = await paymentService.createPayment(users_id, tariff_id);
            
            return res.status(201).json({
                redirect_url: link
            });
        }catch (e) {
            next(e);
        }
    }

    async paymentSuccess(req, res, next){
        try {
            const { confirmation_token } = req.body;

            await paymentService.paymentSuccess(confirmation_token);
            
            return res.status(201).json({
                created: true
            });
        }catch (e) {
            next(e);
        }
    }
}

module.exports = new PaymentController();