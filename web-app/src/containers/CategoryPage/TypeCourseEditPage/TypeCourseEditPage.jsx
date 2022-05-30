import React, { useState, useEffect } from "react";
import styles from "./TypeCourseEditPage.module.css";

import add from "../../../resources/icons/addXS.svg";
import cross from "../../../resources/icons/crossXS.svg";

const TypeCourseEditPage = () => {
  return (
    <div className={styles["category-create-link"]}>
      <div className={styles["item"]}>
        <div className="field-input-cross">
          <input
            name="input-cross"
            type="text"
            className="input-with-cross"
            placeholder="Название категории"
          />
          <img src={cross}></img>
        </div>
        <div className="line" />
        <button className="btn-add-small">
        <img src={add}/> Добавить подкатегорию
      </button>
      </div>



      <button className="btn-add"> Добавить категорию </button>
      <button className="btn-add"> Cохранить </button>
    </div>
  );
};

export default TypeCourseEditPage;
