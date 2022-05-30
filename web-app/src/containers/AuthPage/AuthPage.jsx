import React, { useEffect, useState, useContext } from "react";
import useHttp from "../../hooks/http.hook";
import useMessage from "../../hooks/message.hook";
import AuthContext from "../../context/AuthContext";
import GoogleLogin, { GoogleLogout } from "react-google-login";
import logo_black from "../../resources/images/main/logo_black.svg";
import styles from "./AuthPage.module.css";
import AuthApiConstants from "../../constants/addresses/api/auth.api.constants";

const AuthPage = () => {
  const auth = useContext(AuthContext);
  const message = useMessage();
  const { loading, request, error, clearError } = useHttp();

  const [form, setForm] = useState({
    email: '', password: ''
  });

  useEffect(() => {
    message(error, "error");
    clearError();
  }, [error, message, clearError]);

  const changeHandler = event => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const loginHandler = async () => {
    try {
      const data = await request(AuthApiConstants.login, 'POST', { ...form });

      if (data.message) {
        message(data.message, "error");

        const errors = data.errors;
        if (errors) {
          errors.forEach((item) => {
            message(item.msg, "error");
          });
        }
        return;
      }

      auth.login(data.users_id, data.roles_id, data.tokens, data.modules, data.type_auth);
      message("Авторизация прошла успешно!", "success");
    } catch (e) { }
  };

  /*const loginOAuthHandler = async (response) => {
      try {
          const data = await request(AuthApiConstants.oauth, 'POST', { code: response.code });
          if (data.message) {
              message(data.message, "error");

              const errors = data.errors;
              if (errors) {
                  errors.forEach((item) => {
                      message(item.msg, "error");
                  }); 
              }
              return;
          }

          auth.login(data.users_id, data.roles_id, data.rokens, data.modules, data.type_auth);
          message("Авторизация прошла успешно!", "success");
      } catch (e) { }
  };*/

  return (
    <div className={styles["container"]}>
      <div className={styles["form"]}>
        <span className={styles["entrance-txt"]}>Вход</span>

        <div className={styles["btn"]}>
          Почта
          <div>
            <input
              id="email"
              type="email"
              name="email"
              placeholder="Введите email"
              className={styles["input-mail"]}
              onChange={changeHandler}
            />
          </div>
        </div>
        <div className={styles["btn"]}>
          Пароль
          <input
            id="password"
            type="password"
            name="password"
            placeholder="Введите пароль"
            className={styles["input-pass"]}
            onChange={changeHandler}
          />
        </div>
        <button
          className={styles["btn-enter"]}
          onClick={loginHandler}
        /*disabled={loading}*/
        >
          <span>Войти</span>
        </button>
        {
          //<div className={styles["btn"]}>
          //<GoogleLogin
          //className={styles["btn-google"]}
          /*   clientId={default_config.googleAuthApiKey}*/
          //buttonText="Авторизоваться через Google"
          /* onSuccess={loginOAuthHandler}*/
          //cookiePolicy={"single_host_origin"}
          //responseType="code"
          //accessType="offline"
          //responseType='code'
          //</div>></GoogleLogin>
          //</div>
        }

      </div>
    </div>
  );
};

export default AuthPage;
