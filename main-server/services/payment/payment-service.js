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
const { YooCheckout, PaymentStatuses } = require('@a2seven/yoo-checkout');
const uuid = require('uuid');
const TarrifInfoDto = require('../../dtos/payment/tarrif-info-dto');
const PaymentStatusValueConstants = require('../../constants/values/payment-status-value-constants');
const { DateTime } = require('luxon');
const mailService = require('../mail/mail-service');

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
                    "capture": true,
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


            const link = process.env.LANDING_URL_PAYMENT + "/?confirmation_token=" + payment.confirmation.confirmation_token;
            
            await t.commit();

            return link;
        } catch (e) {
            await t.rollback();
            throw ApiError.BadRequest(e.message);
        }
    }

    async paymentSuccess(confirmationToken) {

        const t = await sequelize.transaction();

        try {
            const checkout = new YooCheckout({
                shopId: process.env.YOOKASSA_SHOP_ID,
                secretKey: process.env.YOOKASSA_SECRET_KEY
            });

            const payment = await PaymentModel.findOne({
                where: {
                    confirmation_token: confirmationToken,
                    status: PaymentStatusValueConstants.proceed
                }
            });

            if (!payment) {
                throw ApiError.BadRequest("Данный платёж не может быть подтверждён из-за завершающего статуса платежа!");
            }

            if (payment.status == PaymentStatusValueConstants.success){
                throw ApiError.BadRequest("Данный платёж не может быть подтверждён, так как он уже подтверждён!");
            }

            const paymentInfo = await checkout.getPayment(payment.payment_id);
            
            // Need change payment status
            if((!paymentInfo.paid) || (paymentInfo.status != PaymentStatuses.succeeded)){
                throw ApiError.BadRequest("Платёж не был завершён!");
            }

            const userInfo = await UsersModel.findOne({
                where: {
                    id: payment.users_id
                }
            });

            if(!userInfo){
                throw ApiError.BadRequest("Данного пользователя не существует!");
            }

            const tariff = await TariffModel.findOne({
                where: {
                    id: payment.tariff_id
                }
            });

            if (!tariff) {
                throw ApiError.BadRequest("Данного тарифа не существует!");
            }

            /*await checkout.capturePayment(
                payment.payment_id,
                {
                    "amount": {
                        "value": tariff.price,
                        "currency": "RUB"
                    }
                },
                payment.idempotenceKey
            );*/

            payment.status = PaymentStatusValueConstants.success;
            await payment.save();

            const currentData = DateTime.local();
            
            await SubscribeModel.create({
                users_id: payment.users_id,
                tariff_id: payment.tariff_id,
                date_activation: currentData.toISO(),
                date_completion: currentData.plus({days: tariff.period}).toISO(),
                is_active: true
            }, {
                transaction: t
            });

            await t.commit();

            await mailService.sentPaymentMail(userInfo.email, tariff.description, tariff.price);

            return true;
        } catch (e) {
            console.log(e);
            await t.rollback();
            throw ApiError.BadRequest(e.message);
        }
    }
}

module.exports = new PaymentService();