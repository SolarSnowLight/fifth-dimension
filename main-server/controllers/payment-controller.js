const ApiError = require('../exceptions/api-error');
const { validationResult } = require('express-validator');
const paymentService = require('../services/payment/payment-service');
const PaymentApiConstants = require('../constants/addresses/server/payment-api-constants');

class PaymentController {
    async postPaymentToken(req, res, next){
        try {
            const { payment_token } = req.body;

            await paymentService.postPaymentToken(payment_token);
            
            return res.status(201).json({
                created: true
            });
        }catch (e) {
            next(e);
        }
    }
}

module.exports = new PaymentController();