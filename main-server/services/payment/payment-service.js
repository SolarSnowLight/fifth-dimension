const ApiError = require('../../exceptions/api-error');
const {
    CategoriesModel, SubcategoriesModel,
    CategoriesSubcategoriesModel,
    CoursesModel, CoursesCategoriesModel,
    CoursesSoundsModel, SoundsModel,
    UsersStatisticsModel, PaymentModel,
    TariffModel, SubscribeModel,
    CoursesDeletedModel, sequelize, Sequelize, UsersModel
} = require('../../data/index');
const { YooCheckout } = require('@a2seven/yoo-checkout');
const uuid = require('uuid');
const TarrifInfoDto = require('../../dtos/payment/tarrif-info-dto');
const PaymentStatusValueConstants = require('../../constants/values/payment-status-value-constants');

class PaymentService {
    async tariffGet() {
        const tarrif = await TariffModel.findAll({
            where: {
                is_active: true
            }
        });

        return tarrif.map((value) => {
            return new TarrifInfoDto(value.dataValues);
        });
    }

    async createPayment(usersId, tariffId) {
        const t = await sequelize.transaction();

        try {
            // Generate idempotence key
            const idempotenceKey = uuid.v4();

            // Create new YooCheckout
            const checkout = new YooCheckout({
                shopId: process.env.YOOKASSA_SHOP_ID,
                secretKey: process.env.YOOKASSA_SECRET_KEY
            });

            // Check for subsrcibe
            const subscribe = await SubscribeModel.findOne({
                where: {
                    users_id: usersId,
                    is_active: true
                }
            });

            if (subscribe) {
                throw ApiError.BadRequest("Данный пользователь уже имеет подписку!")
            }

            const user = await UsersModel.findOne({
                where: {
                    id: usersId
                }
            });

            if (!user) {
                throw ApiError.BadRequest("Данного пользователя не существует!");
            }

            const tariff = await TariffModel.findOne({
                where: {
                    uuid: tariffId,
                    is_active: true
                }
            });

            if (!tariff) {
                throw ApiError.BadRequest("Данного тарифа не существует!");
            }

            // Create new payment
            const payment = await checkout.createPayment(
                {
                    "amount": {
                        "value": tariff.price,
                        "currency": "RUB"
                    },
                    "confirmation": {
                        "type": "embedded",
                    },
                    "capture": false,
                    "description": "Подписка \"" + tariff.description + "\"",
                    "metadata": {
                        "tariff_id": tariff.id
                    }
                    /*
                    for mobile SDK
                    "payment_token": paymentToken,
                    "confirmation": {
                        "type": "redirect",
                        "return_url": "http://localhost:5000"
                    },*/
                },
                idempotenceKey
            );

            // const paymentInfo = await checkout.getPayment(payment.id);

            const [paymentKey, successKey] = [uuid.v4(), uuid.v4()];

            await PaymentModel.create({
                users_id: usersId,
                tariff_id: tariff.id,
                payment_id: payment.id,
                idempotence_key: idempotenceKey,
                payment_key: paymentKey,
                success_key: successKey,
                status: PaymentStatusValueConstants.proceed,
                confirmation_token: payment.confirmation.confirmation_token
            }, {
                transaction: t
            });


            const link = process.env.LANDING_URL_PAYMENT + "/?user_id=" + user.uuid
                + "&confirmation_token=" + payment.confirmation.confirmation_token
                + "&payment_key=" + paymentKey
                + "&success_key=" + successKey
                + "&tariff_id=" + tariff.uuid;
            
            await t.commit();

            return link;
        } catch (e) {
            await t.rollback();
            throw ApiError.BadRequest(e.message);
        }
    }

    async paymentSuccess(usersId, confirmationToken, paymentKey, successKey, tariffId) {
        // Начало транзакции
        const t = await sequelize.transaction();

        try {
            // Create new YooCheckout
            const checkout = new YooCheckout({
                shopId: process.env.YOOKASSA_SHOP_ID,
                secretKey: process.env.YOOKASSA_SECRET_KEY
            });

            // Check for subsrcibe
            const subscribe = await SubscribeModel.findOne({
                where: {
                    users_id: usersId,
                    is_active: true
                }
            });

            if (subscribe) {
                throw ApiError.BadRequest("Данный пользователь уже имеет подписку!")
            }

            // Find tariff
            const tariff = await TariffModel.findOne({
                where: {
                    uuid: tariffId,
                    is_active: true
                }
            });

            if (!tariff) {
                throw ApiError.BadRequest("Данного тарифа не существует!");
            }

            // Find user
            const user = await UsersModel.findOne({
                where: {
                    uuid: usersId
                }
            });

            if (!user) {
                throw ApiError.BadRequest("Данного пользователя не существует!");
            }

            const payment = await PaymentModel.findOne({
                where: {
                    users_id: user.id,
                    tariff_id: tariff.id,
                    confirmation_token: confirmationToken,
                    payment_key: paymentKey,
                    success_key: successKey
                }
            });

            if(!payment){
                throw ApiError.BadRequest("Подтверждение платежа невозможно, так как реквизиты платежа были изменены!");
            }


            // Create new payment
            /*const payment = await checkout.createPayment(
                {
                    "amount": {
                        "value": tariff.price,
                        "currency": "RUB"
                    },
                    "confirmation": {
                        "type": "embedded",
                    },
                    "capture": false,
                    "description": "Подписка \"" + tariff.description + "\"",
                    "metadata": {
                        "tariff_id": tariff.id
                    }
                    
                    for mobile SDK
                    "payment_token": paymentToken,
                    "confirmation": {
                        "type": "redirect",
                        "return_url": "http://localhost:5000"
                    },
                },
                idempotenceKey
            );*/

            // const paymentInfo = await checkout.getPayment(payment.id);

            const [paymentKey, successKey] = [uuid.v4(), uuid.v4()];

            await PaymentModel.create({
                users_id: usersId,
                tariff_id: tariff.id,
                payment_id: payment.id,
                idempotence_key: idempotenceKey,
                payment_key: paymentKey,
                success_key: successKey,
                status: PaymentStatusValueConstants.proceed,
                confirmation_token: payment.confirmation.confirmation_token
            }, {
                transaction: t
            });


            const link = process.env.LANDING_URL_PAYMENT + "/?user_id=" + user.uuid
                + "&confirmation_token=" + payment.confirmation.confirmation_token
                + "&payment_key=" + paymentKey
                + "&success_key=" + successKey
                + "&tariff_id=" + tariff.uuid;
            
            await t.commit();

            return link;
        } catch (e) {
            await t.rollback();
            throw ApiError.BadRequest(e.message);
        }

        try {
            /*const payment = await checkout.createPayment(
                {
                    // "payment_token": paymentToken,
                    "amount": {
                        "value": "5.00",
                        "currency": "RUB"
                    },
                    "confirmation": {
                        "type": "embedded",
                    },
                    "capture": true,
                    "description": "Заказ №1",
                    "metadata": {
                      "order_id": "37"
                    }
                },
                idempotenceKey
            );*/
            //console.log(payment)

            /*const receipt = await checkout.createReceipt(
                {
                    "customer": {
                        "full_name": "Иванов Иван Иванович",
                        "email": "swdaniel@yandex.ru"
                    },
                    "payment_id": paymentId,
                    "type": "payment",
                    "send": true,
                    "items": [
                        {
                            "description": "Подписка 1",
                            "quantity": "1.00",
                            "amount": {
                                "value": "5.00",
                                "currency": "RUB"
                            },
                            "vat_code": "2",
                            "payment_mode": "full_prepayment",
                            "payment_subject": "commodity",
                            "country_of_origin_code": "CN",
                        },
                    ],
                    // 'cashless' | 'prepayment' | 'postpayment' | 'consideration';
                    "settlements": [
                        {
                            "type": "cashless",
                            "amount": {
                                "value": "5.00",
                                "currency": "RUB"
                            },
                        }
                    ]
                },
                idempotenceKey
            );*/

            const paymentInfo = await checkout.getPayment(paymentId);
            console.log(paymentInfo)

            /*const receiptInfo = await checkout.getReceipt(receipt.id);
            console.log(receiptInfo);*/

            /*const paymentGet = await checkout.capturePayment(
                payment.id,
                {
                    "amount": {
                        "value": "1500.00",
                        "currency": "RUB"
                    }
                },
                idempotenceKey
            );

            console.log(paymentGet);*/
        } catch (error) {
            console.log(error);
        }
    }
}

module.exports = new PaymentService();