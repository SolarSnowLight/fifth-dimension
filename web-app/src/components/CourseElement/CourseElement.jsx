import React, { useEffect, useState, useContext, useRef } from "react";
import styles from "./ViewLesson.module.css";
import "react-datepicker/dist/react-datepicker.css";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import cours from "../../resources/images/main/cours.jpg";
import MenuItem from "@mui/material/MenuItem";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import MenuLessonOptionsConstants from "../../constants/values/menu.view.lesson.options.constants";
import CourseApiConstants from "../../constants/addresses/api/course.api.constants";
import MainApiConstants from "../../constants/addresses/api/main.api.constants";
import useHttp from "../../hooks/http.hook";
import AudioElement from "../AudioComponent";

// Для плеера
import play from "../../resources/icons/player/play.svg";
import pause from "../../resources/icons/player/pause.svg";
import audio from "../../resources/audio.mp3";
import { getDurationTime } from "../../utils/audio/AudioFormatter";

const ViewLesson = (props) => {
  const { loading, request, error, clearError } = useHttp();

  const [value, setValue] = useState("1");
  const [currentBtn, setCurrentBtn] = useState(1);
  const [menuItem, setMenuItem] = useState(null);
  const ITEM_HEIGHT = 48;
  const [playAudio, setPlayAudio] = useState(false);
  const [durationAudios, setDurationAudios] = useState([]);

  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  // Хук для работы с audio
  const audioEl = useRef(null);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const onClickMenuItem = (e, option) => {
    handleClose();
  };

  useEffect(() => {
    const data = props.currentCourses;
    if (data) {
      console.log(data);
    }
  }, [props.currentCourses]);

  return (
    <div className={styles["content"]}>
      <div className={styles["courses"]}>
        {
          (props.currentCourses && props.currentCourses.courses && props.currentCourses.courses.map((item) => {
            return (
              <div className={styles["cours"]}>
                <div className={styles["cours_head"]}>
                  <div className={styles["cours_head_text"]}>
                    <div>
                      <span className={styles["cours_head_num"]}>{item.sounds.length + " уроков"}</span>
                      <span className={styles["cours_head_name"]}>
                        <br />
                        {item.title}
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
                        {MenuLessonOptionsConstants.map((option) => (
                          <MenuItem
                            key={option}
                            selected={option === "Pyxis"}
                            onClick={(e) => {
                              onClickMenuItem(e, option);
                            }}
                          >
                            {option}
                          </MenuItem>
                        ))}
                      </Menu>
                    </div>
                  </div>
                  <img src={`${MainApiConstants.main_server}/${item.title_img_path.replace('\\', '/')}`} className={styles["cours_head_img"]} />
                </div>
                {item.sounds && item.sounds.map((itemSound) => {
                  return (
                    <div className={styles["lesson_row"]}>
                      <AudioElement
                        audioUrl={`${MainApiConstants.main_server}/data/files/${itemSound.file_sound_path.split('\\')[2]}`}
                        audioImageUrl={`url(${MainApiConstants.main_server}/data/files/${itemSound.title_img_path.split('\\')[2]})`}
                        audioTitle={itemSound.title}
                        lessonNum={"Урок №" + itemSound.lesson_num}
                      />
                    </div>
                  );
                })}
              </div>
            );
          }))
        }
      </div>
    </div>
  );
};

export default ViewLesson;
