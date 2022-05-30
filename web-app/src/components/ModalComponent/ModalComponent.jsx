import React, { useEffect } from "react";
import classNames from 'classnames';
import styles from './ModalComponent.module.css';

const ModalComponent = ({active, setActive, children}) => {

    return (
        <div className={active ? classNames(styles["modal"], styles["active"]) : styles["modal"]} onClick={() => setActive(false)}>
            <div className={active ? classNames(styles["modal_content"], styles["active"]) : styles["modal_content"]} onClick={(e) => e.stopPropagation()}>
                {children}
            </div>
        </div>
    );
};

export default ModalComponent;