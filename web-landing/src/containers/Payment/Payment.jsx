import { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import styles from './Payment.module.css';

const Payment = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const confirmationToken = searchParams.get("confirmation_token");
    const userId = searchParams.get("user_id");
    const paymentKey = searchParams.get("payment_key");
    const successKey = searchParams.get("success_key");
    const tariffId = searchParams.get("tariffId");

    // Инициализация виджета от ЮKassa. Все параметры обязательные.
    const checkout = new window.YooMoneyCheckoutWidget({
        confirmation_token: confirmationToken, //Токен, который перед проведением оплаты нужно получить от ЮKassa

        error_callback: function (error) {
            console.log(error)
        }
    });


    /* Обработка ситуации успешного платежа пользователем */
    const successHandler = () => {

    };

    /* Обработка ситуации завершения платежа пользователем */
    const completeHandler = () => {
        checkout.destroy();
    };

    useEffect(() => {
        /* Отображение платежной формы в контейнере */
        checkout.render('payment-form');

        /* Обработка ситуации успешного платежа пользователем */
        checkout.on("success", successHandler);

        /* Обработка ситуации завершения платежа пользователем */
        checkout.on("complete", completeHandler);
    }, []);

    return (
        <></>
    )
}

export default Payment;