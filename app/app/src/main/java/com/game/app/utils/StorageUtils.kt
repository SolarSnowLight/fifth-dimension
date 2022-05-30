package com.game.app.utils

import com.game.app.models.course.CourseImgDataModel
import com.game.app.models.course.CourseImgItemModel
import com.game.app.models.course.CourseNoteDataModel
import com.game.app.models.course.CourseNoteItemModel
import com.game.app.models.sound.SoundFileDataModel
import com.game.app.models.sound.SoundFileItemModel
import com.game.app.models.sound.SoundImgItemModel
import com.game.app.models.sound.SoundImgDataModel

fun existsElementById(coursesId: Int, data: CourseImgDataModel): Boolean{
    for(value in data.titleImages){
        if(value.coursesId == coursesId){
            return true
        }
    }

    return false
}

fun getImageByCourse(coursesId: Int, courseImages: CourseImgDataModel?): CourseImgItemModel?{
    if(courseImages == null){
        return null
    }

    for(value in courseImages.titleImages){
        if(value.coursesId == coursesId){
            return value
        }
    }

    return null
}

fun existsElementByIdSoundImg(soundsId: Int, data: SoundImgDataModel?): Boolean{
    if((data == null) || (data.titleImages == null)){
        return false
    }

    for(value in data.titleImages){
        if(value.soundsId == soundsId){
            return true
        }
    }

    return false
}

fun getImageBySound(soundsId: Int, courseImages: SoundImgDataModel?): SoundImgItemModel?{
    if((courseImages == null) || (courseImages.titleImages == null)){
        return null
    }

    for(value in courseImages.titleImages){
        if(value.soundsId == soundsId){
            return value
        }
    }

    return null
}

fun existsElementByIdSoundFile(soundsId: Int, data: SoundFileDataModel): Boolean{
    for(value in data.soundFiles){
        if(value.soundsId == soundsId){
            return true
        }
    }

    return false
}

fun getSoundFileBySound(soundsId: Int, soundsFiles: SoundFileDataModel?): SoundFileItemModel?{
    if((soundsFiles == null) || (soundsFiles.soundFiles == null)){
        return null
    }

    for(value in soundsFiles.soundFiles){
        if(value.soundsId == soundsId){
            return value
        }
    }

    return null
}

/*fun existsElementInContentDataModel(coursesId: Int, coursesNotes: CourseNoteDataModel): CourseNoteItemModel?{
    if((coursesNotes == null) || (coursesNotes.coursesNotes == null)){
        return false
    }

    for(value in soundsFiles.soundFiles){
        if(value.soundsId == soundsId){
            return true
        }
    }

    return false
}*/