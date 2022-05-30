package com.game.app.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.game.app.R
import com.game.app.components.snackbar.CustomSnackBar
import com.game.app.containers.auth.AuthActivity
import com.game.app.containers.auth.fragments.LoginFragment
import com.game.app.containers.base.BaseFragment
import com.game.app.data.UserPreferences
import com.game.app.network.RemoteDataSource
import com.game.app.network.Resource
import com.game.app.network.apis.AuthApi
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// Старт новой активности с определёнными флагами
fun<A : Activity> Activity.startNewActivity(
    activity: Class<A>,
    message: String? = null,
    type: String? = null
){
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        if(message != null){
            it.putExtra("message", message)
        }
        if(type != null){
            it.putExtra("type", type)
        }

        startActivity(it)
    }
}

// Старт новой активности с определёнными флагами
fun<A : Activity> Activity.startStdActivity(
    activity: Class<A>,
    message: String? = null,
    type: String? = null
){
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if(message != null){
            it.putExtra("message", message)
        }
        if(type != null){
            it.putExtra("type", type)
        }

        startActivity(it)
    }
}

fun Activity.handleMessage(
    root: View,
    message: String
){
    Snackbar.make(root, message, Snackbar.LENGTH_LONG).show()
}

fun Activity.handleErrorMessage(
    root: View,
    message: String
){
    CustomSnackBar.make(root.parent as ViewGroup,message,Snackbar.LENGTH_LONG,null,
        R.drawable.ic_error_polygon,null, ContextCompat.getColor(this, R.color.red_color))?.show()
}

fun Activity.handleWarningMessage(
    root: View,
    message: String
){
    CustomSnackBar.make(root.parent as ViewGroup,message,Snackbar.LENGTH_LONG,null,
        R.drawable.ic_warning_polygon,null, ContextCompat.getColor(this, R.color.warning_color))?.show()
}

fun Activity.checkAuth(
    userPreferences: UserPreferences,
    message: String? = null
): Boolean{
    // Проверка на авторизацию пользователя
    val authDataModel = runBlocking {
        userPreferences.authData.first()
    }

    if(authDataModel == null){
        startNewActivity(
            AuthActivity::class.java,
            message,
            "warning"
        )

        return false
    }

    val remoteDataSource = RemoteDataSource()

    // Формирование API для взаимодействия с сервером
    val api = remoteDataSource.buildApi(AuthApi::class.java, userPreferences)

    val response = runBlocking {
        api.authVerification()
    }

    if(response.code() == 401){
        startNewActivity(
            AuthActivity::class.java,
            message,
            "warning"
        )

        return false
    }

    return true
}