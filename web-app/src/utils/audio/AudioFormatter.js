

export const getDurationTime = (durationAudio) => {
    let sec_num = parseInt(durationAudio, 10); // don't forget the second param
    let hours = Math.floor(sec_num / 3600);
    let minutes = Math.floor((sec_num - hours * 3600) / 60);
    let seconds = sec_num - hours * 3600 - minutes * 60;

    if (hours < 10) {
      hours = "0" + String(hours);
    }else if(hours <= 23){
      hours = String(hours);
    }else{
      hours = "00";
    }

    if (minutes < 10) {
      minutes = "0" + String(minutes);
    }else if(minutes <= 59){
      minutes = String(minutes);
    }else{
      minutes = "00";
    }
    
    if (seconds < 10) {
      seconds = "0" + String(seconds);
    }else if(seconds <= 59){
      seconds = String(seconds);
    }else{
      seconds = "00";
    }

    if ((hours === "00")) {
      return minutes + ":" + seconds;
    } else {
      return hours + ":" + minutes + ":" + seconds;
    }
  };