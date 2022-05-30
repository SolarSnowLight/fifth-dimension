import React, { useEffect, useState, useContext, useRef } from "react";
import useHttp from "../../hooks/http.hook";
import useMessage from "../../hooks/message.hook";

import Select from "react-select";
import { getFormattedDate } from "../../utils/date/DateFormatter";
import AuthContext from "../../context/AuthContext";
import ImageUploading from "react-images-uploading";
import DatePicker from "react-datepicker";
import styles from "./CreatorPage.module.css";
import "./custom.scss";
import CreatorLesson from "../../components/CreatorLesson/CreatorLesson";
import CreatorLessonStatic from "../../components/CreatorLesson/CreatorLessonStatic";
import CategoryApiConstants from "../../constants/addresses/api/category.api.constants";
import CategoryService from "../../services/category/category.service";
import CourseService from "../../services/course/course.service";
import ProgressBar from "../../components/ProgressBar";

const CreatorPage = () => {
  // Конфигурирование контейнера
  const auth = useContext(AuthContext);
  const message = useMessage();
  const { loading, request, error, clearError } = useHttp();
  const categoryService = new CategoryService(
    message,
    loading,
    request,
    error,
    clearError
  );

  const courseService = new CourseService(
    message,
    loading,
    request,
    error,
    clearError
  );

  const [activeProgressBar, setActiveProgressBar] = useState(false);

  // Состояния работы модифицирующего блока (уроки)
  const [num, setNum] = useState(1);
  const img = useState(null);
  const title = useState(null);
  const audio = useState(null);
  const description = useState(null);
  const type = useState(null);

  // Информация обо всех созданных уроках
  const [lessons, setLessons] = useState([]);

  // Модификация каждого созданного нового урока
  const numUpdate = useState(null);
  const numDelete = useState(null);

  // ********************************************************************
  // Состояния данных текущего интерфейса создания курса
  const [courseDate, setCourseDate] = useState(new Date());
  const [courseCategory, setCourseCategory] = useState(null);
  const [courseSubCategory, setCourseSubCategory] = useState(null);
  const [courseSubscription, setCourseSubscription] = useState(false);
  const [courseTitle, setCourseTitle] = useState("");
  const [courseDescription, setCourseDescription] = useState("");
  const [courseImage, setCourseImage] = useState([]);
  const [courseType, setCourseType] = useState("");

  // Данные для категорий
  const [selectOptionsCategory, setSelectOptionsCategory] = useState([]);
  const [selectOptionsSubCategory, setSelectOptionsSubCategory] = useState([]);
  const [categories, setCategories] = useState([]);

  // Обработка состояний данных текущего интерфейса создания курса
  const onChangeImage = (imageList, addUpdateIndex) => {
    setCourseImage(imageList);
  };

  const onChangeCourseDate = (value) => {
    if (getFormattedDate(value) < getFormattedDate(new Date())) {
      message("Дата открытия курса не может быть ранее текущей даты!", "error");
      return;
    }

    setCourseDate(value);
  };

  const onChangeCourseCategory = (e) => {
    setCourseCategory(e.label);
    setCourseSubCategory(null);

    setSelectOptionsSubCategory(
      categories[e.value].sub_categories.map((item, index) => {
        return {
          value: index,
          label: item.title,
        };
      })
    );
  };

  const onChangeCourseSubCategory = (e) => {
    setCourseSubCategory(e.label);
  };

  const onChangeCourseSubscription = (e) => {
    setCourseSubscription(!courseSubscription);
  };

  const onChangeCourseTitle = (e) => {
    if (e.target.value.length === 0) {
      message("Курс должен иметь название", "error");
    }

    setCourseTitle(e.target.value);
  };

  const onChangeCourseType = (e) => {
    if (e.target.value.length === 0) {
      message("Курс должен иметь тип", "error");
    }

    setCourseType(e.target.value);
  };

  const onChangeCourseDescription = (e) => {
    if (e.target.value.length === 0) {
      message("Курс должен иметь описание", "error");
    }

    setCourseDescription(e.target.value);
  };
  // ********************************************************************

  // Создание нового курса
  const onCreateCourse = async () => {
    // ---
    // Валидация входных данных
    if (getFormattedDate(courseDate) < getFormattedDate(new Date())) {
      message("Дата открытия курса не может быть ранее текущей даты!", "error");
      return;
    }

    if (courseCategory == null) {
      message("Курс должен быть отнесён к какой-либо категории!", "error");
      return;
    }

    if (
      courseTitle == null ||
      courseDescription == null ||
      courseTitle.length == 0 ||
      courseDescription.length == 0
    ) {
      message("Курс должен иметь название и описание!", "error");
      return;
    }

    if (courseImage.length == 0) {
      message("Курс должен иметь фоновое изображение!", "error");
      return;
    }

    if (courseType.length == 0) {
      message("Курс должен иметь определённый тип", "error");
      return;
    }

    if (lessons.length == 0) {
      message("Курс должен иметь хотя бы 1 урок!", "error");
      return;
    }

    for (let i = 0; i < lessons.length; i++) {
      if (
        (!lessons[i].title) ||
        (lessons[i].title.length == 0) ||
        (!lessons[i].audio) ||
        (!lessons[i].description) ||
        (lessons[i].description.length == 0) ||
        (!lessons[i].type) ||
        (lessons[i].type.length == 0)
      ) {
        message(
          "В созданных уроках содержатся некорректные данные! Необходимо обновить страницу и провести процедуру создания курса с начала",
          "error"
        );
        return;
      }
    }
    // ---

    const data = {
      users_id: auth.usersId,
      roles_id: auth.rolesId,
      date_open: courseDate,
      category: courseCategory,
      subcategory: courseSubCategory,
      type: courseType,
      subscription: courseSubscription,
      description: courseDescription,
      title: courseTitle,
      img: courseImage[0].file,
      lessons: lessons,
    };

    await courseService.createCourse(data);
  };

  // Создание нового урока
  const onCreateLesson = () => {
    if ((!num) || (!title[0])
      || (!audio[0]) || (!img[0])
      || (!description[0]) || (!type[0])) {
      message("Для создания урока необходимо заполнить все поля", "error");
      return;
    }

    lessons.push({
      num: num,
      title: title[0],
      audio: audio[0],
      img: img[0],
      description: description[0],
      type: type[0]
    });

    setLessons(lessons);

    setNum(num + 1);
  };

  // Обновление урока
  const onUpdateLesson = () => {
    if ((!num) || (!title[0])
      || (!audio[0]) || (!img[0])
      || (!description[0]) || (!type[0])) {
      message("Для обновления урока необходимо заполнить все поля", "error");
      return;
    }

    const index = numUpdate[0] - 1;

    lessons[index].title = title[0];
    lessons[index].audio = audio[0];
    lessons[index].img = img[0];
    lessons[index].description = description[0];
    lessons[index].type = type[0];

    setLessons(lessons);

    setNum(lessons.length + 1);
    img[1](null);
    title[1](null);
    audio[1](null);
    description[1](null);
    type[1](null);

    numUpdate[1](null);
  };

  // Обработка обновления урока
  useEffect(() => {
    const value = numUpdate[0];

    if (value) {
      const index = value - 1;

      setNum(value);
      img[1](lessons[index].img);
      title[1](lessons[index].title);
      audio[1](lessons[index].audio);
      description[1](lessons[index].description);
      type[1](lessons[index].type);
    }
  }, [numUpdate[0]]);

  // Обработка удаления урока
  useEffect(() => {
    const value = numDelete[0];

    if (value) {
      const index = value - 1;

      if (index >= 0) {
        lessons.splice(index, 1);
        for (let i = 0; i < lessons.length; i++) {
          lessons[i].num = i + 1;
        }
        setLessons(lessons);
        setNum(num - 1);
      }
    }

    numDelete[1](null);
  }, [numDelete[0]]);

  // Обработка загрузки страницы
  useEffect(async () => {
    // ---
    // Загрузка категорий
    const data = await categoryService.getAllCategory();
    setCategories(data.categories);

    setSelectOptionsCategory(
      data.categories.map((item, index) => {
        return {
          value: index,
          label: item.title,
        };
      })
    );
    // ---
  }, []);

  return (
    <div className={styles["container"]}>
      {
        activeProgressBar && <ProgressBar />
      }

      <div className={styles["info"]}>
        <DatePicker
          onChange={onChangeCourseDate}
          selected={courseDate}
          inline
        />

        <div className={styles["item"]}>
          <div className={styles["categories"]}>
            Категория
            <div>
              <Select
                options={selectOptionsCategory}
                id="categories"
                name="categories"
                placeholder="Выберите категорию"
                className={styles["categories-select"]}
                onChange={onChangeCourseCategory}
                theme={(theme) => ({
                  ...theme,
                  colors: {
                    ...theme.colors,
                    primary25: "#b19472",
                    primary: "#b19472",
                    neutral0: "#FEFEF9",
                    primary50: "#b19472",
                    neutral20: "#b19472",
                  },
                })}
              />
            </div>
          </div>
          <div
            className={styles["subcategories"]}
            style={{
              display: selectOptionsSubCategory.length > 0 ? "block" : "none",
            }}
          >
            Подкатегория
            <div>
              <Select
                options={selectOptionsSubCategory}
                id="subcategories"
                name="subcategories"
                placeholder="Выберите категорию"
                className={styles["categories-select"]}
                onChange={onChangeCourseSubCategory}
                theme={(theme) => ({
                  ...theme,
                  colors: {
                    ...theme.colors,
                    primary25: "#b19472",
                    primary: "#b19472",
                    neutral0: "#FEFEF9",
                    primary50: "#b19472",
                    neutral20: "#b19472",
                  },
                })}
              />
            </div>
          </div>

          <div className={styles["categories"]}>
            <div className={styles["name_container"]}>
              Тип плейлиста
              <input
                className={styles["name_field"]}
                id="name"
                name="name"
                type="text"
                placeholder="Введите тип плейлиста"
                value={courseType}
                onChange={onChangeCourseType}
              />
            </div>
          </div>
          <div className={styles["check"]}>
            Платный
            <div className={styles["checkbox"]}>
              <input
                id="cb1"
                type="checkbox"
                value={courseSubscription}
                onChange={onChangeCourseSubscription}
              />
              <label htmlFor="cb1"></label>
            </div>
          </div>
        </div>
        <div className={styles["item"]}>
          <div className={styles["name_container"]}>
            Название курса
            <input
              className={styles["name_field"]}
              id="name"
              name="name"
              type="text"
              placeholder="Введите название курса"
              value={courseTitle}
              onChange={onChangeCourseTitle}
            />
          </div>
          <div className={styles["about_container"]}>
            Описание
            <textarea
              className={styles["name_field_about"]}
              id="name"
              name="name"
              type="text"
              placeholder="Введите описание курса"
              value={courseDescription}
              onChange={onChangeCourseDescription}
            />
          </div>
        </div>
        <div className={styles["name_container_img"]}>
          Фоновое изображение
          <ImageUploading
            value={courseImage}
            onChange={onChangeImage}
            dataURLKey="data_url"
          >
            {({
              imageList,
              onImageUpload,
              onImageRemoveAll,
              onImageUpdate,
              onImageRemove,
              isDragging,
              dragProps,
            }) => (
              <div>
                <button
                  className={styles["upload_image_wrapper"]}
                  style={{
                    display: courseImage.length > 0 ? "none" : "block",
                  }}

                  onClick={onImageUpload}
                  {...dragProps}
                >
                  Выберите главное фото
                </button>
                {imageList.map((image, index) => (
                  <div key={index}>
                    <img
                      src={image.data_url}
                      alt=""
                      width="343"
                      height="343"
                      className={styles["upload_image"]}
                    />
                    <button
                      onClick={() => onImageRemove(index)}
                      className={styles["upload_image_wrapper_del"]}
                    >
                      Удалить
                    </button>
                  </div>
                ))}
              </div>
            )}
          </ImageUploading>
        </div>
      </div>
      <div className={styles["lessons"]}>
        {lessons.length > 0 &&
          lessons?.map((item, index) => {
            return (
              <div key={index} className={styles["lessons"]}>
                <CreatorLessonStatic
                  data={{
                    num: item.num,
                    img: item.img,
                    title: item.title,
                    audio: item.audio,
                    type: item.type,
                    description: item.description
                  }}
                  numUpdate={numUpdate}
                  numDelete={numDelete}
                />
              </div>
            );
          })}
      </div>
      <div className={styles["lessons"]}>
        <CreatorLesson
          data={{
            num: num,
            img: img,
            title: title,
            audio: audio,
            type: type,
            description: description
          }}
          settings={true}
        />
      </div>
      <div className={styles["btns"]}>
        <button
          style={{ display: !numUpdate[0] ? "block" : "none" }}
          className="secondary-btn"
          onClick={onCreateLesson}
        >
          Добавить урок
        </button>
        <button
          style={{ display: numUpdate[0] ? "block" : "none" }}
          className={styles["btn-add"]}
          onClick={onUpdateLesson}
        >
          Сохранить
        </button>
        <button className="main-btn"
          onClick={async () => {
            setActiveProgressBar(true);
            await onCreateCourse();
            setActiveProgressBar(false);
          }}>
          Создать курс
        </button>
      </div>
    </div>
  );
};

export default CreatorPage;
