import React, { useEffect, useState, useContext, useRef } from "react";
import styles from "./ViewLesson.module.css";
import "react-datepicker/dist/react-datepicker.css";
import CourseComponent from "../CourseComponent";

const ViewLesson = (props) => {
  return (
    <div className={styles["content"]}>
      <div className={styles["courses"]}>
        {
          (props.currentCourses && props.currentCourses.courses && props.currentCourses.courses
            .filter((item) => {
              if (props.currentSubCategory) {
                return (item.subcategory_title === props.currentSubCategory.title);
              }

              return true;
            })
            .map((item) => {
              return (
                <CourseComponent
                  key={item.id}
                  course={item}
                  options={props.props}
                  setInvalidateCourses={props.setInvalidateCourses}
                />
              );
            }))
        }
      </div>
    </div>
  );
};

export default ViewLesson;
