package com.game.app.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.game.app.MainActivity
import com.game.app.R
import com.game.app.data.NotificationPreferences
import com.game.app.data.UserPreferences
import com.game.app.models.course.CourseDataModel
import com.game.app.models.notification.NotificationModel
import com.game.app.network.RemoteDataSource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class UpdateContentWork(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context,
    workerParams
) {

    private val notificationPreferences = NotificationPreferences(context)
    private val remoteDataSource = RemoteDataSource()
    private var api: UserApi = remoteDataSource.buildApi(UserApi::class.java, null, false)

    companion object{
        const val CHANNEL_ID = "NOTIFICATION_ID"
        const val NOTIFICATION_ID = 101
        const val CHANNEL_NAME = "CHANNEL 101"
        const val CHANNEL_DESCRIPTION = "Notification for update content of course"
    }

    override suspend fun doWork(): Result {
        try{
            val notification = Gson().fromJson(
                runBlocking {
                    notificationPreferences.notification.first()
                },
                NotificationModel::class.java
            )

            if((notification != null) && notification.isReceiveNotification){
                val response = api.getNewCourses()

                if ((response.isSuccessful)
                    && (response.body() != null)
                    && (response.code() == 201)
                ) {
                    val courses = Gson().fromJson(
                        Gson().toJson(
                            JsonParser.parseString(response.body()?.string())
                        ),
                        CourseDataModel::class.java
                    )

                    if(courses.courses.size > 0){
                        val randomIndex = Random.nextInt(0, courses.courses.size)

                        showNotification(
                            "Новый курс \"${courses.courses[randomIndex].title}\"",
                            "Нажмите для просмотра курса в категории \"${courses.courses[randomIndex].categoryTitle}\""
                        )
                    }
                }
            }
        }catch(ex: Exception){
            return Result.failure()
        }

        return Result.success()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel(channelName: String, channelDescription: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance).apply {
                description = channelDescription
            }

            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String = "", text: String = ""){
        val intent = Intent(
            applicationContext,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, 0
        )

        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.layout_notification)
        remoteViews.setTextViewText(R.id.notification_title, title)
        remoteViews.setTextViewText(R.id.notification_content, text)
        remoteViews.setOnClickPendingIntent(R.id.notification, pendingIntent)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setCustomContentView(remoteViews)
            .setSmallIcon(R.drawable.ic_logo)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setAutoCancel(true)

        createNotificationChannel(
            channelName = CHANNEL_NAME,
            channelDescription = CHANNEL_DESCRIPTION
        )

        with(NotificationManagerCompat.from(applicationContext)){
            notify(NOTIFICATION_ID, notification.build())
        }
    }
}