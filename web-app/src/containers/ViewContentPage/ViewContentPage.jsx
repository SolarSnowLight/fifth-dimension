import React, { useState, useEffect } from "react";
import styles from "./ViewContentPage.module.css";
import ViewLesson from "../../components/ViewLesson";

import MenuLessonOptionsConstants from "../../constants/values/menu.view.lesson.options.constants";
import NavbarCategories from "../../components/NavbarCategories";

const ViewContentPage = () => {
  const [value, setValue] = useState("1");
  const [currentCourses, setCurrentCourses] = useState(null);
  const [currentCategory, setCurrentCategory] = useState(null);
  const [currentSubCategory, setCurrentSubCategory] = useState(null);
  const [invalidateCourses, setInvalidateCourses] = useState(false);

  return (
    <div className={styles["content"]}>
      <NavbarCategories
        setCurrentCourses={setCurrentCourses}
        setCurrentCategory={setCurrentCategory}
        setCurrentSubCategory={setCurrentSubCategory}
        setInvalidateCourses={setInvalidateCourses}
        invalidateCourses={invalidateCourses}
      />
      <ViewLesson
        props={MenuLessonOptionsConstants}
        currentCourses={currentCourses}
        currentCategory={currentCategory}
        currentSubCategory={currentSubCategory}
        setInvalidateCourses={setInvalidateCourses}
      />
    </div>
  );
};

export default ViewContentPage;
