package com.game.app.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.game.app.R
import com.game.app.components.snackbar.CustomSnackBar
import com.game.app.containers.auth.fragments.LoginFragment
import com.game.app.containers.base.BaseFragment
import com.game.app.network.Resource
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
){
    when{
        failure.isNetworkError -> {
            handleErrorMessage("Пожалуйста, проверьте интернет соединение")
            // requireView().snackbar("Пожалуйста, проверьте интернет соединение", retry)
        }
        failure.errorCode == 401 -> {
            if(this is LoginFragment){
                handleErrorMessage("Пользователь не авторизован")
            }else{
                (this as BaseFragment<*, *, *>).logout()
            }
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().snackbar(error)
        }
    }
}

fun Fragment.handleMessage(
    message: String
){
    requireView().snackbar(message)
}


fun Fragment.handleErrorMessage(
    message: String
){
    CustomSnackBar.make(this.requireView(), message, Snackbar.LENGTH_LONG,null,
        R.drawable.ic_error_polygon,null, ContextCompat.getColor(requireContext(), R.color.red_color))?.show()
}

fun Fragment.handleWarningMessage(
    message: String
){
    CustomSnackBar.make(this.requireView(), message, Snackbar.LENGTH_LONG,null,
        R.drawable.ic_warning_polygon,null, ContextCompat.getColor(requireContext(), R.color.warning_color))?.show()
}

fun Fragment.hideKeyboard(){
    // Скрытие клавиатуры, перед выводом сообщения об ошибке
    val imm: InputMethodManager =
        activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun Fragment.navigation(
    resId: Int,
    args: Bundle? = null
){
    view?.findNavController()?.navigate(resId, args)
}

// Запись файла в локальное хранилище мобильного устройства
fun Fragment.writeResponseBodyToStorage(body: ResponseBody,
                                        filename: String,
                                        environment: String = Environment.DIRECTORY_PICTURES,
                                        type: String = ".jpg"): String? {
    return try {
        val path = requireActivity().getExternalFilesDir(environment).toString() + File.separator + filename + type
        val filePath = File(path)

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(4096)
            var fileSizeDownloaded: Long = 0
            inputStream = body.byteStream()
            outputStream = FileOutputStream(filePath)
            while (true) {
                val read: Int = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
            }
            outputStream.flush()

            path
        } catch (e: Exception) {
            null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    } catch (e: Exception) {
        null
    }
}