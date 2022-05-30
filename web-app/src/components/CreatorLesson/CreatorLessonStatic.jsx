import React, { useEffect, useState, useContext, useRef } from "react";
import ImageUploading from "react-images-uploading";

import Select from "react-select";
import styles from "./CreatorLesson.module.css";
import "react-datepicker/dist/react-datepicker.css";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import MenuLessonOptionsConstants from "../../constants/values/menu.lesson.options.constants";
import { CircularProgressbarWithChildren } from "react-circular-progressbar";

import play from "../../resources/icons/player/play.svg";
import pause from "../../resources/icons/player/pause.svg";
import { getDurationTime } from "../../utils/audio/AudioFormatter";

const CreatorLessonStatic = (props) => {
  const num = props.data.num;
  const img = props.data.img;
  const title = props.data.title;
  const audio = props.data.audio;
  const type = props.data.type;
  const description = props.data.description;
  const [numUpdate, setNumUpdate] = props.numUpdate;
  const [numDelete, setNumDelete] = props.numDelete;
  const [audioUrl, setAudioUrl] = useState(null);

  const [playAudio, setPlayAudio] = useState(false);
  const [durationAudio, setDurationAudio] = useState('');
  const [currentTime, setCurrentTime] = useState(0);
  const audioEl = useRef(null);

  const [anchorEl, setAnchorEl] = useState(null);
  const ITEM_HEIGHT = 48;
  const open = Boolean(anchorEl);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const onClickMenuItem = (e, option) => {
    switch (option) {
      case 'Удалить': {
        setNumDelete(num);
        break;
      }
      case 'Редактировать': {
        setNumUpdate(num);
        break;
      }
    }
    handleClose();
  }

  useEffect(() => {
    if (audio) {
      if (typeof audio !== 'string') {
        setAudioUrl(URL.createObjectURL(audio));
      } else {
        setAudioUrl(audio);
      }
    }
  }, [audio]);

  return (
    <div className={styles["lesson"]}>
      <div className={styles["head_name_setting"]}>
        {`№${num}`}
        <div>
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
      <div className={styles["lesson_field"]}>
        <div className={styles["lesson_name_file_container"]}>
          Фото
          <ImageUploading
            value={img}
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
              // write your building UI
              <div>
                {(imageList.map) && imageList.map((image, index) => (
                  <div key={index}>
                    <img
                      src={image.data_url}
                      alt=""
                      width="343"
                      height="343"
                      className={styles["lesson_upload_image"]}
                    />
                  </div>
                ))}
              </div>
            )}
          </ImageUploading>
          <div className={styles["categories"]}>
            Тип аудиофайла
            <input
              className={styles["lesson_name_field"]}
              id="name"
              name="name"
              type="text"
              placeholder="Введите название урока"
              value={type ?? ""}
              readOnly
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
              readOnly
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
              readOnly
            />
          </div>
          <div className={styles["lesson_file_container"]}>
            <div>Медиафайл</div>
            <div className={styles["lesson_audio_container"]}>
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
                  src={audioUrl}
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
                            setPlayAudio(!playAudio);
                            audioEl.current.play();
                          }}
                          style={{
                            backgroundImage: `url(${props.data.img[0].data_url})`,
                          }}
                        >
                          <img src={play} className={styles["lesson_player_img"]} />
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
                            setPlayAudio(!playAudio);
                            audioEl.current.pause();
                          }}
                          style={{
                            backgroundImage: `url(${props.data.img[0].data_url})`,
                          }}
                        >
                          <img src={pause} className={styles["lesson_player_img"]} />
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
    </div>
  );
};

export default CreatorLessonStatic;
