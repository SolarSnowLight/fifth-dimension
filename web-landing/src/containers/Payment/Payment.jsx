import { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import MainApiConstants from '../../constants/api/main.api';
import PaymentApiConstants from '../../constants/api/payment.api';
import styles from './Payment.module.css';

const Payment = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const confirmationToken = searchParams.get("confirmation_token");

    // Инициализация виджета от ЮKassa. Все параметры обязательные.
    const checkout = new window.YooMoneyCheckoutWidget({
        confirmation_token: confirmationToken, //Токен, который перед проведением оплаты нужно получить от ЮKassa

        error_callback: function (error) {
            console.log(error)
        }
    });

    /* Обработка ситуации успешного платежа пользователем */
    const successHandler = async() => {
        const response = await fetch(
            (MainApiConstants.main_server + PaymentApiConstants.payment_success),
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    confirmation_token: confirmationToken
                })
            }
        );

        if(response.ok){
            // redirect to ok
        }

        // redirect to non ok
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