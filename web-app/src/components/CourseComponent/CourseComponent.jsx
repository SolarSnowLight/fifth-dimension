import React, { useEffect, useState, useContext, useRef } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./CourseComponent.module.css";
import "react-datepicker/dist/react-datepicker.css";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import MenuLessonOptionsConstants from "../../constants/values/menu.view.lesson.options.constants";
import MainApiConstants from "../../constants/addresses/api/main.api.constants";
import AudioElement from "../AudioComponent";
import CreatorRoutesConstants from "../../constants/addresses/routes/creator.routes.constants";
import ModalComponent from "../ModalComponent";
import CourseService from "../../services/course/course.service";
import useHttp from "../../hooks/http.hook";
import AuthContext from "../../context/AuthContext";
import useMessage from "../../hooks/message.hook";

const ViewLesson = (props) => {
  const auth = useContext(AuthContext);
  const message = useMessage();
  const { loading, request, error, clearError } = useHttp();
  const courseService = new CourseService(
    message,
    loading,
    request,
    error,
    clearError
  );

  const navigate = useNavigate();

  const ITEM_HEIGHT = 48;
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const [modalActive, setModalActive] = useState(false);
  const [modalText, setModalText] = useState(null);
  const [changeCourse, setChangeCourse] = useState(null);

  const onClickMenuItem = (e, option, data) => {
    switch (option) {
      case 'Редактировать': {
        // Открытие нового компонента с передачей параметров
        navigate(CreatorRoutesConstants.content_edit, { state: { course: data } });
        break;
      }

      case 'Архивировать': {
        setModalText("архивировать");
        setModalActive(true);
        setChangeCourse(data);
        break;
      }

      case 'Восстановить': {
        setModalText("восстановить");
        setModalActive(true);
        setChangeCourse(data);
        break;
      }

      case 'Удалить': {
        setModalText("удалить");
        setModalActive(true);
        setChangeCourse(data);
        break;
      }
    }
    handleClose();
  };

  return (
    <div className={styles["cours"]}>
      <ModalComponent active={modalActive} setActive={setModalActive}>
        <div className={styles["delete_course_block"]}>
          <p>Вы действительно хотите {modalText} курс "{(changeCourse) ? changeCourse.title : ""}"</p>
          <div className={styles["delete_course_buttons"]}>
            <button
              onClick={async (e) => {
                setModalActive(false);
                if (modalText === "архивировать") {
                  await courseService.archiveCourse({
                    ...changeCourse,
                    roles_id: auth.rolesId,
                    users_id: auth.usersId
                  });
                } else if (modalText === "восстановить") {
                  await courseService.recoverCourse({
                    ...changeCourse,
                    roles_id: auth.rolesId,
                    users_id: auth.usersId
                  });
                } else if (modalText === "удалить") {
                  await courseService.deleteCompletelyCourse({
                    ...changeCourse,
                    roles_id: auth.rolesId,
                    users_id: auth.usersId
                  });
                }

                props.setInvalidateCourses(true);
              }}
            >{(modalText) ? (modalText.charAt(0).toUpperCase() + modalText.slice(1)) : ""}</button>

            <button style={{
              marginLeft: "10px"
            }}
              onClick={(e) => {
                setChangeCourse(null);
                setModalText(null);
                setModalActive(false);
              }}
            >Отмена</button>
          </div>
        </div>
      </ModalComponent>

      <div className={styles["cours_head"]}>
        <div className={styles["cours_head_text"]}>
          <div>
            <span className={styles["cours_head_num"]}>{props.course.sounds.length + " аудиофайлов"}</span>
            <span className={styles["cours_head_name"]}>
              <br />
              {props.course.title}
            </span>
          </div>
          <div className={styles["cours_settings"]}>
            <IconButton
              aria-label="more"
              id="long-button"
              aria-controls={open ? "long-menu" : undefined}
              aria-expanded={open ? "true" : undefined}
              aria-haspopup="true"
              onClick={handleClick}
            >
              <MoreVertIcon />
            </IconButton>
            <Menu
              id="long-menu"
              MenuListProps={{
                "aria-labelledby": "long-button",
              }}
              anchorEl={anchorEl}
              open={open}
              onClose={handleClose}
              PaperProps={{
                style: {
                  maxHeight: ITEM_HEIGHT * 4.5,
                  width: "20ch",
                },
              }}
            >
              {props.options.map((option) => (
                <MenuItem
                  key={option}
                  selected={option === "Pyxis"}
                  onClick={(e) => {
                    onClickMenuItem(e, option, props.course);
                  }}
                >
                  {option}
                </MenuItem>
              ))}
            </Menu>
          </div>
        </div>
        <img src={`${MainApiConstants.main_server}/${props.course.title_img_path.replace('\\', '/')}`} className={styles["cours_head_img"]} />
      </div>
      {props.course.sounds && props.course.sounds.map((itemSound) => {
        return (
          <div
            key={itemSound.id}
            className={styles["lesson_row"]}
          >
            <AudioElement
              audioUrl={
                `${MainApiConstants.main_server}/data/files/${itemSound.file_sound_path.split('\\')[2]}`
                //`${MainApiConstants.main_server}/${itemSound.file_sound_path}`
              }
              audioImageUrl={
                `url(${MainApiConstants.main_server}/data/files/${itemSound.title_img_path.split('\\')[2]})`
                //`url(${MainApiConstants.main_server}/${itemSound.title_img_path})`
              }
              audioTitle={itemSound.title}
              audioType={itemSound.type}
              lessonNum={"№" + itemSound.lesson_num}
            />
          </div>
        );
      })}
    </div>
  );
};

export default ViewLesson;
