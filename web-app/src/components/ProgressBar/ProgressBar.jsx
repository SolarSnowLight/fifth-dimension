import React from "react";
import { CircularProgressbar, buildStyles } from "react-circular-progressbar";
import ProgressProvider from "../../components/ProgressProvider";
import AnimatedProgressProvider from "../AnimatedProgressProvider";
import ChangingProgressProvider from "../ChangingProgressProvider";
import { easeQuadInOut } from "d3-ease";

const ProgressBar = () => {
    return (
        <div style={{
            width: 70,
            height: 70,
            position: "fixed",
            left: "50%",
            right: "50%",
            top: "50%",
            bottom: "50%",
            zIndex: 1
        }}>
            <ChangingProgressProvider values={[0, 20, 40, 60, 80, 100]}>
                {percentage => (
                    <CircularProgressbar
                        value={percentage}
                        styles={
                            buildStyles({
                                pathColor: "green"
                            })
                        }
                    />
                )}
            </ChangingProgressProvider>
        </div>
    )
}

export default ProgressBar;