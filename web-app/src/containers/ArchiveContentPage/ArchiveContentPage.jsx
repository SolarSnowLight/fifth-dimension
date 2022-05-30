import React, { useEffect, useState, useContext } from "react";
import { useHttp } from "../../hooks/http.hook";
import { useMessage } from "../../hooks/message.hook";
import { AuthContext } from "../../context/AuthContext";
import styles from "./ArchiveContentPage.module.css";
import ViewLesson from "../../components/ViewLesson";
import NavbarCategories from "../../components/NavbarCategories";
import MenuLessonDeletedOptions from "../../constants/values/menu.lesson.deleted.options.constants";

const ArchiveContentPage = () => {
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
        deleted={true}
      />
      <ViewLesson
        props={MenuLessonDeletedOptions}
        currentCourses={currentCourses}
        currentCategory={currentCategory}
        currentSubCategory={currentSubCategory}
        setInvalidateCourses={setInvalidateCourses}
      />
    </div>
  );
};
export default ArchiveContentPage;
