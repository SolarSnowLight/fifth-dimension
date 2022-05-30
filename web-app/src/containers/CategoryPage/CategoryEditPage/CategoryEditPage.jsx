import React, { useState, useEffect } from "react";
import useMessage from "../../../hooks/message.hook";
import useHttp from "../../../hooks/http.hook";
import CategoryService from "../../../services/category/category.service";
import ModalComponent from "../../../components/ModalComponent";

import styles from "./CategoryEditPage.module.css";

import add from "../../../resources/icons/addXS.svg";
import cross from "../../../resources/icons/crossXS.svg";

const CategoryEditPage = () => {
  const message = useMessage();
  const { loading, request, error, clearError } = useHttp();
  const categoryService = new CategoryService(
    message,
    loading,
    request,
    error,
    clearError
  );

  let [currentCategory, setCurrentCategory] = useState(null);
  const [currentElement, setCurrentElement] = useState(null);

  const [modalActive, setModalActive] = useState(false);
  const [modalText, setModalText] = useState(null);

  const [activeProgressBar, setActiveProgressBar] = useState(false);
  const [updateBtn, setUpdateBtn] = useState(true);

  useEffect(async () => {
    // ---
    // Загрузка категорий
    const data = await categoryService.getAllCategory();

    setCurrentCategory(data.categories);
    // ---
  }, []);

  const invalidateCurrentCategory = () => {
    setCurrentCategory(JSON.parse(JSON.stringify(currentCategory)));
  };

  const refreshCurrentCategory = async () => {
    const data = await categoryService.getAllCategory();
    setCurrentCategory(data.categories);
  };

  return (
    <div className={styles["category-create-link"]}>
      <br />
      {currentCategory &&
        currentCategory.map((value) => {
          return (
            <div key={value.id} className={styles["item"]}>
              <div>
                <div className="field-input-cross">
                  <input
                    name="input-cross"
                    type="text"
                    className="input-with-cross"
                    defaultValue={value.title}
                    placeholder="Название категории"
                    onChange={(e) => {
                      value.title = e.target.value;
                      value.description = e.target.value;

                      invalidateCurrentCategory();
                      setUpdateBtn(false);
                    }}
                  />
                  <img
                    src={cross}
                    onClick={(e) => {
                      if (value.id < 1) {
                        currentCategory = currentCategory.filter((item) => {
                          return item.id != value.id;
                        });

                        invalidateCurrentCategory();
                        return;
                      }

                      setCurrentElement(value);
                      setModalText("категорию");
                      setModalActive(true);
                    }}
                  ></img>
                </div>
              </div>
              <div className="line" />
              <div className={styles["sub-item-container"]}>
                {value.sub_categories &&
                  value.sub_categories.map((sub_value) => {
                    return (
                      <div key={sub_value.id} className="field-input-cross">
                        <input
                          name="input-cross"
                          type="text"
                          className="input-with-cross"
                          defaultValue={sub_value.title}
                          onChange={(e) => {
                            sub_value.title = e.target.value;
                            sub_value.description = e.target.value;

                            invalidateCurrentCategory();
                            setUpdateBtn(false);
                          }}
                          placeholder="Название подкатегории"
                        />
                        <img
                          src={cross}
                          onClick={(e) => {
                            if (sub_value.id < 1) {
                              let data = currentCategory.find((data) => {
                                return data.id === value.id;
                              });

                              data.sub_categories = data.sub_categories.filter(
                                (item) => {
                                  return item.id != sub_value.id;
                                }
                              );

                              invalidateCurrentCategory();
                              return;
                            }

                            setCurrentElement({
                              ...sub_value,
                              category_id: value.id,
                            });
                            setModalText("подкатегорию");
                            setModalActive(true);
                          }}
                        ></img>
                      </div>
                    );
                  })}
              </div>
              <button className="btn-add-small">
                <img
                  className="cursor-pointer"
                  src={add}
                  onClick={(e) => {
                    let data = currentCategory.find((data) => {
                      return data.id === value.id;
                    });

                    data.sub_categories.push({
                      id: -Math.random(),
                      description: null,
                      title: "",
                    });

                    invalidateCurrentCategory();
                    setUpdateBtn(false);
                  }}
                />{" "}
                Добавить подкатегорию
              </button>
            </div>
          );
        })}

      <button
        className="btn-add-small"
        onClick={(e) => {
          currentCategory.push({
            id: -Math.random(),
            description: null,
            title: "",
            sub_categories: [],
          });

          invalidateCurrentCategory();
          setUpdateBtn(false);
        }}
      >
        {" "}
        <img className="cursor-pointer" src={add} />
        Добавить категорию{" "}
      </button>

      <div className={styles["btn-save"]}>
        <button
          className="btn-add cursor-pointer"
          onClick={async () => {
            setActiveProgressBar(true);
            await categoryService.updateCategory(currentCategory);
            await refreshCurrentCategory();
            setActiveProgressBar(false);
            setUpdateBtn(true);
          }}
          disabled={updateBtn}
        >
          {" "}
          Cохранить{" "}
        </button>
      </div>

      <ModalComponent active={modalActive} setActive={setModalActive}>
        <div className={styles["delete_course_block"]}>
          <p>
            Вы действительно хотите удалить {modalText} "
            {currentElement ? currentElement.title : ""}"?
          </p>
          <p>
            Внимание! Эта процедура приведёт к удалению всех курсов, которые
            связаны с данной категорией / подкатегорией"
          </p>
          <div className={styles["delete_course_buttons"]}>
            <button
              onClick={async (e) => {
                setModalActive(false);
                setUpdateBtn(false);
                if (modalText === "категорию") {
                  currentCategory = currentCategory.filter((item) => {
                    return item.id != currentElement.id;
                  });

                  invalidateCurrentCategory();
                } else if (modalText === "подкатегорию") {
                  let data = currentCategory.find((data) => {
                    return data.id === currentElement.category_id;
                  });

                  data.sub_categories = data.sub_categories.filter((item) => {
                    return item.id != currentElement.id;
                  });

                  invalidateCurrentCategory();
                }

                //props.setInvalidateCourses(true);
              }}
            >
              Удалить
            </button>

            <button
              style={{
                marginLeft: "10px",
              }}
              onClick={(e) => {
                //setChangeCourse(null);
                setModalText(null);
                setModalActive(false);
              }}
            >
              Отмена
            </button>
          </div>
        </div>
      </ModalComponent>
    </div>
  );
};

export default CategoryEditPage;
