import React from "react";
import styles from "./Content.module.css";
import PrivacyPolicy from "../PrivacyPolicy";

import {BrowserRouter, Routes, Route, NavLink } from "react-router-dom";


import icon from "../../resources/logo_black.svg";
import left from "../../resources/left.png";
import right from "../../resources/right.png";

const Content = () => {
  
  return (
    <div className={styles["body"]}>
      <div className={styles["item-img-left"]}>
      <img className={styles["img-left"]} src={left}/>
      </div>

      <div className={styles["content"]}>
        <div className={styles["txt-icon"]}>
        <div className={styles["icon"]}>
        <img src={icon} />
      </div>
      <div className={styles["item text"]}>
        <h1>
          “Пятое измерение” - практики <br />
          способные изменить твою <br />
          жизнь к лучшему
        </h1>
        <h2>
          Саморазвивайся в обасти семьи, бизнеса, <br />
          духовного здоровья, эмойций и <br />
          внутреннего потенциала.
        </h2>
        </div>
     
      </div>
  <NavLink to="pp" element={PrivacyPolicy} className={styles["pp"]}> Политика конфиденциальности </NavLink>
      </div>
      <div className={styles["item-img-right"]}>
      <img className={styles["img-right"]} src={right}/>
      </div>
    </div>
  );
};

export default Content;
