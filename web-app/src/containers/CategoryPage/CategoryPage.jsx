import React, { useState, useEffect } from "react";
import styles from "./CategoryPage.module.css";
import ViewLesson from "../../components/ViewLesson";

import MenuLessonOptionsConstants from "../../constants/values/menu.view.lesson.options.constants";
import NavbarCategories from "../../components/NavbarCategories";
import CategoryEditPage from "./CategoryEditPage";
import ProgressBar from "../../components/ProgressBar";

const CategoryPage = () => {
  const [value, setValue] = useState("1");
  const [currentCourses, setCurrentCourses] = useState(null);
  const [currentCategory, setCurrentCategory] = useState(null);
  const [currentSubCategory, setCurrentSubCategory] = useState(null);
  const [invalidateCourses, setInvalidateCourses] = useState(false);
  const [currentLink, setCurrentLink] = useState({
    url: "Категории"
  });

  return (
    <div className={styles["content"]}>
      <CategoryEditPage />
    </div >
  );
};

export default CategoryPage;
