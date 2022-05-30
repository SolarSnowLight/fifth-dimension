package com.game.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.asLiveData
import androidx.work.*
import com.game.app.containers.home.HomeActivity
import com.game.app.containers.start.StartActivity
import com.game.app.data.StartPreferences
import com.game.app.databinding.ActivityMainBinding
import com.game.app.models.user.UserOpenAppModel
import com.game.app.services.UpdateContentWork
import com.game.app.utils.enable
import com.game.app.utils.startNewActivity
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object{
        const val UNIQUE_WORK_ID = "UPDATE_CONTENT_WORK"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_App)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start unique periodic work for get notification about update content
        initialWorkManager()

        binding.progressBar.enable(true)

        val startPreferences = StartPreferences(this)

        startPreferences.firstOpen.asLiveData().observe(this){
            binding.progressBar.enable(false)

            if(it == null){
                startNewActivity(StartActivity::class.java)
            }else{
                val data = Gson().fromJson(
                    it,
                    UserOpenAppModel::class.java
                )

                startNewActivity(if (data.firstOpen) StartActivity::class.java else HomeActivity::class.java)
            }
        }
    }

    private fun initialWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(
            UpdateContentWork::class.java,
            6,
            TimeUnit.HOURS
        ).setConstraints(constraints)
            .build()

        /*val workRequest = OneTimeWorkRequest.Builder(
            UpdateContentWork::class.java
        ).setConstraints(constraints)
            .build()*/

        WorkManager.getInstance(this)
            //.enqueue(workRequest)
            .enqueueUniquePeriodicWork(
                UNIQUE_WORK_ID,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}