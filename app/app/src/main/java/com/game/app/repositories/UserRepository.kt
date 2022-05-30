package com.game.app.repositories

import com.game.app.models.course.CourseIdModel
import com.game.app.models.course.CourseTitleModel
import com.game.app.models.date.DateModel
import com.game.app.models.payment.PaymentTokenModel
import com.game.app.models.sound.SoundAvailableModel
import com.game.app.models.sound.SoundCompleteModel
import com.game.app.models.sound.SoundIdModel
import com.game.app.models.user.UserIdModel
import com.game.app.models.user.UserInfoUpdateModel
import com.game.app.network.apis.UserApi
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepository(
    private val api: UserApi
) : BaseRepository() {
    suspend fun getCoursesByTitle(title: String) = safeApiCall {
        val requestBody = Gson().toJson(
            CourseTitleModel(
                title = title
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.getCoursesByTitle(requestBody)
    }

    suspend fun getCoursesByDate(date: String) = safeApiCall {
        val requestBody = Gson().toJson(
            DateModel(
                date = date
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.getCoursesByDate(requestBody)
    }

    suspend fun getCourseTitleImage(coursesId: Int) = safeApiCall {
        val requestBody = Gson().toJson(
            CourseIdModel(
                coursesId = coursesId
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.getCourseTitleImage(requestBody)
    }

    suspend fun getAllCategory() = safeApiCall {
        api.getAllCategory()
    }

    suspend fun getUserInfo(usersId: String) = safeApiCall {
        val requestBody = Gson().toJson(
            UserIdModel(
                usersId = usersId
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.getUserInfo(requestBody)
    }

    suspend fun updateUserInfo(usersId: String, name: String, surname: String, password: String? = null) = safeApiCall {
        val requestBody = Gson().toJson(
            UserInfoUpdateModel(
                usersId = usersId,
                name = name,
                surname = surname,
                password = password
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.updateUserInfo(requestBody)
    }

    suspend fun getAllSounds(coursesId: Int) = safeApiCall {
        val requestBody = Gson().toJson(
            CourseIdModel(
                coursesId = coursesId
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.getAllSounds(requestBody)
    }

    suspend fun getTitleImgSound(soundsId: Int) = safeApiCall {
        val requestBody = Gson().toJson(
            SoundIdModel(
                soundsId = soundsId
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.getTitleImgSound(requestBody)
    }

    suspend fun getSoundFile(usersId: String, coursesId: Int, soundsId: Int) = safeApiCall {
        val requestBody = Gson().toJson(
            SoundAvailableModel(
                usersId = usersId,
                coursesId = coursesId,
                soundsId = soundsId
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.getSoundFile(requestBody)
    }

    suspend fun setCompleteSound(usersId: String, coursesId: Int, soundsId: Int) = safeApiCall {
        val requestBody = Gson().toJson(
            SoundCompleteModel(
                usersId = usersId,
                coursesId = coursesId,
                soundsId = soundsId
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.setCompleteSound(requestBody)
    }

    suspend fun checkSubscribe(usersId: String) = safeApiCall {
        val requestBody = Gson().toJson(
            UserIdModel(
                usersId = usersId
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.checkSubscribe(requestBody)
    }

    suspend fun paymentToken(paymentToken: String) = safeApiCall {
        val requestBody = Gson().toJson(
            PaymentTokenModel(
                paymentToken = paymentToken
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        api.paymentToken(requestBody)
    }
}