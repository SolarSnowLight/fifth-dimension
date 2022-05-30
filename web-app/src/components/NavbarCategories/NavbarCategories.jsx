import React, { useEffect, useState, useContext, useRef } from "react";

import styles from "./NavbarCategories.module.css";
import "react-datepicker/dist/react-datepicker.css";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import cours from "../../resources/images/main/cours.jpg";
import MenuItem from "@mui/material/MenuItem";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import useMessage from "../../hooks/message.hook";
import useHttp from "../../hooks/http.hook";
import CategoryService from "../../services/category/category.service";
import CourseApiConstants from "../../constants/addresses/api/course.api.constants";

//для плеера
import play from "../../resources/icons/player/play.svg";
import pause from "../../resources/icons/player/pause.svg";
import audio from "../../resources/audio.mp3";

const NavbarCategories = (props) => {
  const message = useMessage();
  const { loading, request, error, clearError } = useHttp();
  const categoryService = new CategoryService(message, loading, request, error, clearError);

  const setCurrentCategory = props.setCurrentCategory;
  const setCurrentSubCategory = props.setCurrentSubCategory;
  const invalidateCourses = props.invalidateCourses;
  const setInvalidateCourses = props.setInvalidateCourses;

  const [value, setValue] = React.useState("1");
  const [currentBtn, setCurrentBtn] = useState(0);
  const [currentBtnSub, setCurrentBtnSub] = useState(0);
  const [playAudio, setPlayAudio] = useState(false);
  const [durationAudio, setDurationAudio] = useState("");
  const [menuItem, setMenuItem] = useState(null);
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);
  const ITEM_HEIGHT = 48;

  // Загрузка информации о конкретных курсах
  const getCoursesInfo = async (title) => {
    try {
      const data = await request(
        (
          (props.deleted) ?
            CourseApiConstants.get_all_title_deleted
            : CourseApiConstants.get_all_title
        ), 'POST', {
        title: title
      });

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

      return data;
    } catch (e) { }
  };


  // Данные для категорий
  const [subCategories, setSubCategories] = useState([]);
  const [categories, setCategories] = useState([]);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const onClickMenuItem = (e, option) => {
    handleClose();
  };

  // Обработка загрузки страницы
  useEffect(async () => {
    // ---
    // Загрузка категорий
    const data = await categoryService.getAllCategory();

    setCategories(data.categories);
    setSubCategories(data.categories[currentBtn].sub_categories);

    setCurrentCategory(data.categories[currentBtn]);
    setCurrentSubCategory(
      ((data.categories[currentBtn].sub_categories) && (data.categories[currentBtn].sub_categories.length > 0)) ?
        data.categories[currentBtn].sub_categories[0]
        : null
    )

    props.setCurrentCourses(await getCoursesInfo(data.categories[currentBtn].title));
    // ---
  }, []);

  // Обработка обновления курсов
  useEffect(async () => {
    if ((categories.length == 0) || (!invalidateCourses)) {
      return;
    }

    props.setCurrentCourses(await getCoursesInfo(categories[currentBtn].title));
    setInvalidateCourses(false);
  }, [invalidateCourses]);

  return (
    <div className={styles["content"]}>
      <div className={styles["categories_1"]}>
        {
          (categories.length > 0) && categories.map((item, index) => {
            return (
              <button
                key={index}
                className={
                  currentBtn === index
                    ? styles["category-border_1"]
                    : styles["category_1"]
                }
                onClick={async () => {
                  setCurrentBtn(index);
                  setCurrentBtnSub(0);
                  setSubCategories(categories[index].sub_categories);

                  setCurrentCategory(categories[index]);
                  setCurrentSubCategory(
                    ((categories[index].sub_categories) && (categories[index].sub_categories.length > 0)) ?
                      categories[index].sub_categories[0]
                      : null
                  )
                  props.setCurrentCourses(await getCoursesInfo(categories[index].title));
                }}
              >
                {item.title}
              </button>
            );
          })
        }
      </div>
      <hr className={styles["line"]}></hr>
      <div className={styles["categories"]}>
        {
          (subCategories.length > 0) && subCategories.map((item, index) => {
            return (
              <button
                key={index}
                className={
                  currentBtnSub === index
                    ? styles["category-border"]
                    : styles["category"]
                }
                onClick={() => {
                  setCurrentSubCategory(item);
                  setCurrentBtnSub(index);
                }}
              >
                {item.title}
              </button>
            );
          })
        }
      </div>
    </div>
  );
};

export default NavbarCategories;
