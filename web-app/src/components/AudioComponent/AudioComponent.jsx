import React, { useEffect, useState, useContext, useRef } from "react";
import styles from '../ViewLesson/ViewLesson.module.css';
import "react-datepicker/dist/react-datepicker.css";
import { CircularProgressbarWithChildren } from 'react-circular-progressbar';
import 'react-circular-progressbar/dist/styles.css';

//для плеера
import play from "../../resources/icons/player/play.svg";
import pause from "../../resources/icons/player/pause.svg";
import { getDurationTime } from "../../utils/audio/AudioFormatter";

const AudioComponent = (props) => {
    const [playAudio, setPlayAudio] = useState(false);
    const [durationAudio, setDurationAudio] = useState("");
    const [currentTime, setCurrentTime] = useState(0);
    const audioEl = useRef(null);

    return (
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
                src={props.audioUrl}
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
                                    backgroundImage: props.audioImageUrl,
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
                                    backgroundImage: props.audioImageUrl,
                                }}
                            >
                                <img src={pause} className={styles["lesson_player_img"]} />
                            </button>
                        </CircularProgressbarWithChildren>
                    </div>
                )}
            </div>
            <div className={styles["lesson_audio_text"]}>
                <span className={styles["lesson_audio_text_num"]}>{props.lessonNum}.{" " + props.audioType}</span>
                <span className={styles["lesson_audio_text_name"]}>
                    <br />
                    {props.audioTitle}
                </span>
            </div>
            <span className={styles["lesson_player_duration"]}>
                {getDurationTime(durationAudio - currentTime)}
            </span>
        </div>
    );
};

export default AudioComponent;