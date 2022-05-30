package com.game.app.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

// Старт новой активности с определёнными флагами
fun<A : Context> Context.startStdActivity(
    activity: Class<A>,
    data: String? = null
){
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if(data != null){
            it.putExtra("data", data)
        }

        startActivity(it)
    }
}