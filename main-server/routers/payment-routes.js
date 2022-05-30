const Router = require('express');
const router = new Router();
const { check, body } = require('express-validator');
const paymentController = require('../controllers/payment-controller');
const PaymentApiConstants = require('../constants/addresses/server/payment-api-constants');
const authMiddleware = require('../middlewares/auth/auth-middleware');

// Маршрут: /payment/token
router.post(
    PaymentApiConstants.payment_token,
    [
        authMiddleware,
    ],
    paymentController.postPaymentToken
);

module.exports = router;