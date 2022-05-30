import React, { useEffect, useState, useContext, useRef } from "react";
import { useLocation } from "react-router-dom";
import useHttp from "../../../hooks/http.hook";
import useMessage from "../../../hooks/message.hook";

import Select from "react-select";
import { getFormattedDate } from "../../../utils/date/DateFormatter";
import AuthContext from "../../../context/AuthContext";
import ImageUploading from "react-images-uploading";
import DatePicker from "react-datepicker";
import styles from "./CreatorEditPage.module.css";
import "./custom.scss";
import CreatorLesson from "../../../components/CreatorLesson/CreatorLesson";
import CreatorLessonEdit from "../../../components/CreatorLesson/CreatorLessonEdit";
import CreatorLessonStatic from "../../../components/CreatorLesson/CreatorLessonStatic";
import CategoryService from "../../../services/category/category.service";
import CourseService from "../../../services/course/course.service";
import MainApiConstants from "../../../constants/addresses/api/main.api.constants";
import ProgressBar from "../../../components/ProgressBar";

const CreatorEditPage = () => {
    const location = useLocation();
    const course = location.state.course;

    const [activeProgressBar, setActiveProgressBar] = useState(false);

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

    // Информация обо всех созданных уроках
    const [lessons, setLessons] = useState(
        course.sounds.map((item) => {
            return {
                id: item.id,
                num: item.lesson_num,
                img: [{
                    data_url: `${MainApiConstants.main_server}/${item.title_img_path}`
                }],
                title: item.title,
                audio: `${MainApiConstants.main_server}/${item.file_sound_path}`,
                description: item.description,
                type: item.type
            }
        })
    );

    // Состояния работы модифицирующего блока (уроки)
    const [num, setNum] = useState(lessons.length + 1);
    const img = useState(null);
    const title = useState("");
    const audio = useState(null);
    const description = useState(null);
    const type = useState(null);

    // Модификация каждого созданного нового урока
    const numUpdate = useState(null);
    const numDelete = useState(null);

    // ********************************************************************
    // Состояния данных текущего интерфейса создания курса
    const [courseDate, setCourseDate] = useState(new Date(course.date_open));
    const [courseCategory, setCourseCategory] = useState(course.category_title);
    const [courseSubCategory, setCourseSubCategory] = useState(course.subcategory_title);
    const [courseSubscription, setCourseSubscription] = useState(course.subscribe);
    const [courseType, setCourseType] = useState(course.type);
    const [courseTitle, setCourseTitle] = useState(course.title);
    const [courseDescription, setCourseDescription] = useState(course.description);
    const [courseImage, setCourseImage] = useState([
        {
            data_url: `${MainApiConstants.main_server}/${course.title_img_path.replace('\\', '/')}`
        }
    ]);

    // Данные для категорий
    const [selectOptionsCategory, setSelectOptionsCategory] = useState([]);
    const [selectOptionsSubCategory, setSelectOptionsSubCategory] = useState([]);
    const [categories, setCategories] = useState([]);

    // Enable/disable update button
    const [updateBtn, setUpdateBtn] = useState(true);

    // Обработка состояний данных текущего интерфейса создания курса
    const onChangeImage = async (imageList, addUpdateIndex) => {
        setUpdateBtn(false);

        setCourseImage(imageList);
    };

    const onChangeCourseDate = (value) => {
        setUpdateBtn(false);

        if (getFormattedDate(value) < getFormattedDate(new Date(course.date_open))) {
            message("Дата открытия курса не может быть ранее текущей даты!", "error");
            return;
        }

        setCourseDate(value);
    };

    const onChangeCourseCategory = (e) => {
        setUpdateBtn(false);

        setCourseCategory(e.label);

        const index = categories.find((value) => {
            return (value.title === e.label);
        });

        setCourseSubCategory(((index) && (index.sub_categories.length > 0)) ? index.sub_categories[0].title : null);

        setSelectOptionsSubCategory(
            ((index) && (index.sub_categories.length > 0)) ?
                index.sub_categories.map((item, index) => {
                    return {
                        value: index,
                        label: item.title,
                    };
                })
                : []
        );
    };

    const onChangeCourseSubCategory = (e) => {
        setUpdateBtn(false);

        setCourseSubCategory(e.label);
    };

    const onChangeCourseSubscription = (e) => {
        setUpdateBtn(false);

        setCourseSubscription(!courseSubscription);
    };

    const onChangeCourseType = (e) => {
        setUpdateBtn(false);

        setCourseType(e.target.value);
    };

    const onChangeCourseTitle = (e) => {
        setUpdateBtn(false);

        if (e.target.value.length === 0) {
            message("Курс должен иметь название", "error");
        }

        setCourseTitle(e.target.value);
    };

    const onChangeCourseDescription = (e) => {
        setUpdateBtn(false);

        if (e.target.value.length === 0) {
            message("Курс должен иметь описание", "error");
        }

        setCourseDescription(e.target.value);
    };
    // ********************************************************************

    // Создание нового курса
    const onUpdateCourse = async () => {
        // ---
        // Валидация входных данных
        /*if (getFormattedDate(courseDate) < getFormattedDate(new Date())) {
            message("Дата открытия курса не может быть ранее текущей даты!", "error");
            return;
        }*/

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

        if (
            courseType == null ||
            courseType.length == 0
        ) {
            message("Курс должен иметь тип!", "error");
            return;
        }


        if (courseImage.length == 0) {
            message("Курс должен иметь фоновое изображение!", "error");
            return;
        }

        if (lessons.length == 0) {
            message("Курс должен иметь хотя бы 1 урок!", "error");
            return;
        }

        for (let i = 0; i < lessons.length; i++) {
            if (
                !lessons[i].title ||
                lessons[i].title.length == 0 ||
                !lessons[i].audio ||
                !lessons[i].type ||
                lessons[i].type.length == 0
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
            courses_id: course.id,
            users_id: auth.usersId,
            roles_id: auth.rolesId,
            date_open: courseDate,
            category: courseCategory,
            subcategory: courseSubCategory,
            type: courseType,
            subscription: courseSubscription,
            description: courseDescription,
            title: courseTitle,
            img: courseImage[0].file ?? null,
            lessons: lessons.map((value) => {
                return {
                    ...value,
                    audio: (typeof value.audio === 'string') ? null : value.audio,
                    img: (value.img[0].file) ? value.img[0] : null
                }
            }),
        };

        await courseService.updateCourse(data);
        setUpdateBtn(true);
    };

    // Создание нового урока
    const onCreateLesson = () => {
        setUpdateBtn(false);

        if ((!num) || (!title[0])
            || (!audio[0]) || (!img[0])
            || (!description[0]) || (!type[0])) {
            message("Для создания урока необходимо заполнить поля", "error");
            return;
        }

        lessons.push({
            num: num,
            title: title[0],
            audio: audio[0],
            img: img[0],
            type: type[0],
            description: description[0]
        });

        setLessons(lessons);

        setNum(num + 1);
    };

    // Обновление урока
    const onUpdateLesson = () => {
        setUpdateBtn(false);

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
        setUpdateBtn(false);
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

        // setUpdateBtn(true);
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
                                defaultInputValue={courseCategory}
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
                                defaultInputValue={courseSubCategory}
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
                    <div className={styles["check"]}>
                        Платный
                        <div className={styles["checkbox"]}>
                            <input
                                id="cb1"
                                type="checkbox"
                                value={courseSubscription ?? false}
                                defaultChecked={courseSubscription ?? false}
                                onChange={onChangeCourseSubscription}
                            />
                            <label htmlFor="cb1"></label>
                        </div>
                    </div>
                    <div className={styles["check"]}>
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
                            value={courseTitle ?? ""}
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
                            value={courseDescription ?? ""}
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
                {
                    lessons.length > 0 &&
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
                    })
                }
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
                    className={styles["btn-add"]}
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
                <button
                    className={styles["btn-create"]}
                    onClick={async () => {
                        setActiveProgressBar(true);
                        await onUpdateCourse();
                        setActiveProgressBar(false);
                        setUpdateBtn(false);
                    }}
                    disabled={updateBtn}
                >
                    Применить
                </button>
            </div>
        </div>
    );
};

export default CreatorEditPage;
