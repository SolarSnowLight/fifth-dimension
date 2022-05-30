import React, { useEffect, useState, useContext } from "react";
import styles from "./Header.module.css";
import logo from "../../resources/images/main/logo_black.svg";

const Header = () => {
  return (
    <div className={styles["container"]}>
          <div className={styles["content"]}>
          <img className={styles["img"]} src={logo}/>
          </div>

    </div>
  );
};

export default Header;
