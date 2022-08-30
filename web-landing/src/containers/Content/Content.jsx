import React from "react";
import styles from "./Content.module.css";
import PrivacyPolicy from "../PrivacyPolicy";

import { BrowserRouter, Routes, Route, NavLink } from "react-router-dom";

import icon from "../../resources/logo_black.svg";
import left from "../../resources/left.png";
import right from "../../resources/right.png";
import UserAgreement from "../UserAgreement";
import MainApiConstants from "../../constants/api/main.api";

const Content = () => {
  return (
    <div className={styles["container"]}>
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
              Саморазвивайся в обасти здоровье, любовь,<br />
              отношения, деньги, концентрация,<br />
              предназначение. Здесь ты найдёшь  <br />
              для себя Свои личные ключи к Счастью. <br />
              <br />
            </h2>
          </div>
          <a href={`${MainApiConstants.main_server}/data/app/app.apk`} download>
            <button
              className={styles["btn"]}
              onClick={async () => {
                /*const resp = await fetch(
                  'http://localhost:5000/api/user/download/app',
                  {
                    method: "GET"
                  }
                );*/

                //console.log(resp);
              }}
            > Скачать приложение для Android</button>
          </a>
        </div>

        <NavLink to="/5measurement/policy" element={PrivacyPolicy} className={styles["pp"]}>
          {" "}
          Политика конфиденциальности{" "}
        </NavLink>
        <NavLink to="/5measurement/agreement" element={UserAgreement} className={styles["pp2"]}>
          {" "}
          Пользовательское соглашение{" "}
        </NavLink>
      </div>
    </div>
  );
};

export default Content;
