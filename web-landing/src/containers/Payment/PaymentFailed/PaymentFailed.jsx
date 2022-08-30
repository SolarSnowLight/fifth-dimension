import styles from './PaymentFailed.module.css';

const PaymentFailed = () => {
    return (
        <div className={styles['payment-block-main']}>
            <div className={styles['payment-block']}>
                <div>
                    <h1 className={styles['payment-text-success']}>Ошибка подтверждения платежа!</h1>
                </div>
                <div>
                    <span>При подтверждении платежа была совершена ошибка! 
                        Если с Вашей карты были сняты средства и Вы увидели данное сообщение просьба обратиться
                         с сообщением по почте kristina.berdsk@mail.ru с предоставлением скриншотов 
                          пришедших к Вам СМС с попыткой подтверждения платежа, а также факт снятия денежных средств
                           и оплаты именно по данному приложению, а также email пользователя, по которому был зарегистрирован
                            Ваш аккаунт в системе.</span>
                </div>
            </div>
        </div>
    )
}

export default PaymentFailed;