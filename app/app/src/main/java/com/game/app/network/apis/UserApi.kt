package com.game.app.network.apis

import com.game.app.constants.network.user.CategoryConstants
import com.game.app.constants.network.user.CourseConstants
import com.game.app.constants.network.user.PaymentConstants
import com.game.app.constants.network.user.UserConstants
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface UserApi {
    // ***
    // Loading data:
    //
    // Getting all courses of a certain category by title
    @POST(CourseConstants.GET_COURSES_TITLE)
    suspend fun getCoursesByTitle(@Body requestBody: RequestBody): Response<ResponseBody>

    // Getting courses by date
    @POST(CourseConstants.GET_COURSES_DATE)
    suspend fun getCoursesByDate(@Body requestBody: RequestBody): Response<ResponseBody>

    // The function of getting information about all categories
    @POST(CategoryConstants.GET_ALL_CATEGORY)
    suspend fun getAllCategory(): Response<ResponseBody>

    // The function of obtaining user information
    @POST(UserConstants.USER_INFO)
    suspend fun getUserInfo(@Body requestBody: RequestBody): Response<ResponseBody>

    // The function for updating user data
    @POST(UserConstants.UPDATE_INFO)
    suspend fun updateUserInfo(@Body requestBody: RequestBody): Response<ResponseBody>

    // The function of getting information about all sound lessons of a particular course
    @POST(CourseConstants.GET_ALL_SOUNDS)
    suspend fun getAllSounds(@Body requestBody: RequestBody): Response<ResponseBody>


    // ****
    // Uploading files:
    //
    // The function of uploading an image of a specific course
    @Streaming
    @POST(CourseConstants.GET_COURSE_TITLE_IMAGE)
    suspend fun getCourseTitleImage(@Body requestBody: RequestBody): Response<ResponseBody>

    // The function of uploading an image of a specific lesson
    @Streaming
    @POST(CourseConstants.GET_TITLE_IMG_SOUND)
    suspend fun getTitleImgSound(@Body requestBody: RequestBody): Response<ResponseBody>

    // Audio file download function
    @Streaming
    @POST(CourseConstants.GET_SOUND)
    suspend fun getSoundFile(@Body requestBody: RequestBody): Response<ResponseBody>

    // ***
    // Statistics:
    @POST(UserConstants.COMPLETE_SOUND)
    suspend fun setCompleteSound(@Body requestBody: RequestBody): Response<ResponseBody>
    // ***

    @POST(UserConstants.NEW_COURSES)
    suspend fun getNewCourses(): Response<ResponseBody>

    @POST(UserConstants.CHECK_SUBSCRIBE)
    suspend fun checkSubscribe(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST(PaymentConstants.PAYMENT_TOKEN)
    suspend fun paymentToken(@Body requestBody: RequestBody): Response<ResponseBody>
}