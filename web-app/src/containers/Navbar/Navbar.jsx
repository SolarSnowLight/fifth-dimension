import React, { useEffect, useState, useContext } from "react";
import { useHttp } from "../../hooks/http.hook";
import styles from "./Navbar.module.css";
import { NavLink } from "react-router-dom";
import AuthContext from "../../context/AuthContext";
import CreatorRoutesConstants from "../../constants/addresses/routes/creator.routes.constants";

//import icons
import menu from "../../resources/icons/menu.svg";
import add from "../../resources/icons/add.svg";
import archive from "../../resources/icons/archive.svg";
import out from "../../resources/icons/out.svg";
import category from "../../resources/icons/category.svg";

const Navbar = () => {
  const auth = useContext(AuthContext);
  const [currentLink, setCurrentLink] = useState({
    url: CreatorRoutesConstants.content_add
  });

  return (
    <div className={styles["container"]}>
      <NavLink to={CreatorRoutesConstants.content_view} className={styles["active"]}>
        <button
          className={
            (currentLink.url == CreatorRoutesConstants.content_view) ? styles["btn-active"] : styles["btn"]
          }
          onClick={() => {
            setCurrentLink({
              url: CreatorRoutesConstants.content_view
            })
          }}
        >
          <img className={styles["icon"]} src={menu} />
          <text className={styles["text"]} >Контент</text>
        </button>
      </NavLink>

      <NavLink to={CreatorRoutesConstants.content_add} className={styles["active"]}>
        <button
          className={
            (currentLink.url == CreatorRoutesConstants.content_add) ? styles["btn-active"] : styles["btn"]
          }
          onClick={() => {
            setCurrentLink({
              url: CreatorRoutesConstants.content_add
            })
          }}
        >
          <img className={styles["icon"]} src={add} />
          <text className={styles["text"]} >Новое</text>
        </button>
      </NavLink>

      <NavLink to={CreatorRoutesConstants.content_category} className={styles["active"]}>
        <button
          className={
            (currentLink.url == CreatorRoutesConstants.content_category) ? styles["btn-active"] : styles["btn"]
          }
          onClick={() => {
            setCurrentLink({
              url: CreatorRoutesConstants.content_category
            })
          }}
        >
          <img className={styles["icon"]} src={category} style={{width: '30px'}}/>
          <text className={styles["text"]} >Категории</text>
        </button>
      </NavLink>

      <NavLink to={CreatorRoutesConstants.content_archive} className={styles["active"]}>
        <button
          className={
            (currentLink.url == CreatorRoutesConstants.content_archive) ? styles["btn-active"] : styles["btn"]
          }
          onClick={() => {
            setCurrentLink({
              url: CreatorRoutesConstants.content_archive
            })
          }}>
          <img className={styles["icon"]} src={archive} />
          <text className={styles["text"]}>Архив</text>
        </button>
      </NavLink>

      <button
        className={styles["btn"]}
        onClick={() => {
          auth.logout();
        }}
      >
        <img className={styles["icon"]} src={out} />
        <text className={styles["text"]} >Выйти</text>
      </button>
    </div>
  );
};

export default Navbar;
