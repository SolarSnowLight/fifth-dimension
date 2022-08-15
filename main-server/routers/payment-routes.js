const Router = require('express');
const router = new Router();
const { check, body } = require('express-validator');
const paymentController = require('../controllers/payment-controller');
const PaymentApiConstants = require('../constants/addresses/server/payment-api-constants');
const authMiddleware = require('../middlewares/auth/auth-middleware');

// Маршрут: /payment/tarrif/get
router.post(
    PaymentApiConstants.tarrif_get,
    [
        authMiddleware
    ],
    paymentController.postTarrifGet
);

// Маршрут: /payment/create
router.post(
    PaymentApiConstants.create,
    [
        authMiddleware,
    ],
    paymentController.postCreatePayment
);

// Маршрут: /payment/success
router.post(
    PaymentApiConstants.success,
    paymentController.paymentSuccess
)

module.exports = router;