import styles from './PaymentSuccess.module.css';

const PaymentSuccess = () => {

    return (
        <div className={styles['payment-block-main']}>
            <div className={styles['payment-block']}>
                <div>
                    <h1 className={styles['payment-text-success']}>Платёж успешно подтверждён!</h1>
                </div>
                <div>
                    <span>Поздравляем! Вы приобрели подписку и теперь Вам доступны все дополнительные материалы!</span>
                </div>
            </div>
        </div>
    )
}

export default PaymentSuccess;