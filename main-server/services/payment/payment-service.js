const ApiError = require('../../exceptions/api-error');
const {
    CategoriesModel, SubcategoriesModel,
    CategoriesSubcategoriesModel,
    CoursesModel, CoursesCategoriesModel,
    CoursesSoundsModel, SoundsModel,
    UsersStatisticsModel,
    TariffModel, SubscribeModel,
    CoursesDeletedModel, Sequelize
} = require('../../data/index');
const { YooCheckout } = require('@a2seven/yoo-checkout');
const uuid = require('uuid');

const idempotenceKey = uuid.v4();

class PaymentService {
    async postPaymentToken(paymentToken) {
        console.log(paymentToken);

        const checkout = new YooCheckout({
            shopId: process.env.YOOKASSA_SHOP_ID,
            secretKey: process.env.YOOKASSA_SECRET_KEY
        });

        try {
            const payment = await checkout.createPayment(
                {
                    "payment_token": paymentToken,
                    "amount": {
                        "value": "1500.00",
                        "currency": "RUB"
                    },
                    "confirmation": {
                        "type": "redirect",
                        "return_url": "https://www.merchant-website.com/return_url"
                    },
                    "capture": false,
                    "description": "Заказ №1",
                    "metadata": {
                      "order_id": "37"
                    }
                },
                idempotenceKey
            );
            console.log(payment)

            const paymentInfo = await checkout.getPayment(payment.id);
            console.log(paymentInfo)

            const paymentGet = await checkout.capturePayment(
                payment.id,
                {
                    "amount": {
                        "value": "1500.00",
                        "currency": "RUB"
                    }
                },
                idempotenceKey
            );

            console.log(paymentGet);
        } catch (error) {
            console.error(error);
        }
    }
}

module.exports = new PaymentService();