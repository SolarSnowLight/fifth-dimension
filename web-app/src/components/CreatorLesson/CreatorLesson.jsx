import React, { useEffect, useState, useContext, useRef } from "react";

import Select from "react-select";
import ImageUploading from "react-images-uploading";

import styles from "./CreatorLesson.module.css";
import "react-datepicker/dist/react-datepicker.css";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import MenuLessonOptionsConstants from "../../constants/values/menu.lesson.options.constants";
import useHttp from "../../hooks/http.hook";
import AdminApiConstants from "../../constants/addresses/api/admin.api.constants";
import play from "../../resources/icons/player/play.svg";
import pause from "../../resources/icons/player/pause.svg";
import { getDurationTime } from "../../utils/audio/AudioFormatter";
import { CircularProgressbarWithChildren } from "react-circular-progressbar";

/*
 * Функциональный компонент для создания и редактирования уроков
 * */

const CreatorLesson = (props) => {
  // Хук взаимодействия с сетью
  const { loading, request, error, clearError } = useHttp();

  // Общие состояния компонента
  const num = props.data.num;
  const [img, setImg] = props.data.img;
  const [title, setTitle] = props.data.title;
  const [audio, setAudio] = props.data.audio;
  const [type, setType] = props.data.type;
  const [description, setDescription] = props.data.description;
  const [audioUrl, setAudioUrl] = useState(audio);

  // Состояния для audio
  const [playAudio, setPlayAudio] = useState(false);
  const [durationAudio, setDurationAudio] = useState("");
  const [currentTime, setCurrentTime] = useState(0);

  // Хук для работы с audio
  const audioEl = useRef(null);

  // Menu
  const [menuItem, setMenuItem] = useState(null);
  const ITEM_HEIGHT = 48;

  const onChangeImg = async (image, addUpdateIndex) => {
    setImg(image);
  };

  const onChangeTitle = (e) => {
    setTitle(e.target.value);
  };

  const onChangeType = (e) => {
    setType(e.target.value);
  }

  const onChangeDescription = (e) => {
    setDescription(e.target.value);
  };

  const onChangeAudio = (e) => {
    if (e.target.files[0]) {
      setAudio(e.target.files[0]);
      setAudioUrl(URL.createObjectURL(e.target.files[0]));
    }
  };

  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

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
    if (audio) {
      if (typeof audio !== "string") {
        setAudioUrl(URL.createObjectURL(audio));
      }
    }
  }, [audio]);

  return (
    <div className={styles["lesson"]}>
      <div className={styles["head_name_setting"]}>{`№${num}`}</div>
      <div className={styles["lesson_field"]}>
        <div className={styles["lesson_name_file_container"]}>
        <div>
          Фото
          <ImageUploading
            value={img}
            onChange={onChangeImg}
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
                  className={styles["btn_lesson_upload"]}
                  onClick={onImageUpload}
                  {...dragProps}
                  style={{
                    display: img ? "none" : "block",
                  }}
                >
                  Выберите фото
                </button>
                {imageList.map((image, index) => (
                  <div key={index}>
                    <img
                      src={image.data_url}
                      alt=""
                      width="343"
                      height="343"
                      className={styles["lesson_upload_image"]}
                    />
                    <button
                      onClick={() => {
                        onImageRemove(index);
                        setImg(null);
                      }}
                      className={styles["btn_lesson"]}
                    >
                      Удалить
                    </button>
                  </div>
                ))}
              </div>
            )}
          </ImageUploading>
          </div>
          <div className={styles["categories"]}>
            Тип аудиофайла
            <input
              className={styles["lesson_name_field"]}
              id="name"
              name="name"
              type="text"
              placeholder="Введите название урока"
              value={type ?? ""}
              onChange={onChangeType}
            />
          </div>
          <div className={styles["lesson_name_container"]}>
            Название урока
            <input
              className={styles["lesson_name_field"]}
              id="name"
              name="name"
              type="text"
              placeholder="Введите название урока"
              value={title ?? ""}
              onChange={onChangeTitle}
            />
          </div>
        </div>
        <div className={styles["lesson_name_file_container"]}>
          <div className={styles["about_container"]}>
            Описание
            <textarea
              className={styles["name_field_about"]}
              id="name"
              name="name"
              type="text"
              placeholder="Введите описание курса"
              value={description ?? ""}
              onChange={onChangeDescription}
            />
          </div>
          <div className={styles["lesson_name_container"]}>
            <div>Медиафайл</div>
            <div className={styles["btn_lesson"]}>
            <label htmlFor="name_audio" className={styles["btn_lesson"]}>
                Выберите медиафайл
              </label>
            </div>
              <input
                style={{display: "none" }}
                id="name_audio"
                type="file"
                accept="audio/mp3"
                onChange={onChangeAudio}
              />
              <div className={styles["lesson_player"]}>
                <audio
                  id="player"
                  className={styles["lesson_player_audio"]}
                  onDurationChange={(e) => {
                    setDurationAudio(e.target.duration);
                  }}
                  onTimeUpdate={(e) => {
                    setCurrentTime(audioEl.current.currentTime);
                  }}
                  onEnded={() => {
                    setCurrentTime(0);
                    setPlayAudio(false);
                  }}
                  ref={audioEl}
                  src={audioUrl ?? audio}
                />
                <div>
                  {!playAudio ? (
                    <div style={{ width: 60, height: 60 }}>
                      <CircularProgressbarWithChildren
                        value={Math.floor(currentTime)}
                        minValue={0}
                        maxValue={Math.floor(durationAudio)}
                      >
                        <button
                          className={styles["lesson_player_play_btn"]}
                          onClick={() => {
                            if (audioUrl || audio) {
                              setPlayAudio(!playAudio);
                              audioEl.current.play();
                            }
                          }}
                          style={{
                            backgroundImage: img
                              ? `url(${img[0].data_url})`
                              : "none",
                          }}
                        >
                          <img
                            src={play}
                            className={styles["lesson_player_img"]}
                          />
                        </button>
                      </CircularProgressbarWithChildren>
                    </div>
                  ) : (
                    <div style={{ width: 60, height: 60 }}>
                      <CircularProgressbarWithChildren
                        value={Math.floor(currentTime)}
                        minValue={0}
                        maxValue={Math.floor(durationAudio)}
                      >
                        <button
                          className={styles["lesson_player_play_btn"]}
                          onClick={() => {
                            if (audioUrl || audio) {
                              setPlayAudio(!playAudio);
                              audioEl.current.pause();
                            }
                          }}
                          style={{
                            backgroundImage: img
                              ? `url(${img[0].data_url})`
                              : "none",
                          }}
                        >
                          <img
                            src={pause}
                            className={styles["lesson_player_img"]}
                          />
                        </button>
                      </CircularProgressbarWithChildren>
                    </div>
                  )}
                </div>
                <span className={styles["lesson_player_duration"]}>
                  {getDurationTime(durationAudio - currentTime)}
                </span>
              </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreatorLesson;
