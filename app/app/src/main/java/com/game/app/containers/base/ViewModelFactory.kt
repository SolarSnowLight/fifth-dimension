package com.game.app.containers.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.game.app.containers.auth.models.AuthViewModel
import com.game.app.containers.home.fragments.home.models.CourseViewModel
import com.game.app.containers.home.models.CalendarViewModel
import com.game.app.containers.home.models.HomeViewModel
import com.game.app.containers.home.models.ProfileViewModel
import com.game.app.repositories.AuthRepository
import com.game.app.repositories.BaseRepository
import com.game.app.repositories.UserRepository
import java.lang.IllegalArgumentException

/*
* Элемент ViewModelFactory используется для возможности передавать ViewModel,
* которые закреплены за различными фрагментами и активностями определённые
* данные, которые могут быть переданны в виде аргументов
* */

class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository as UserRepository) as T
            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> CalendarViewModel(repository as UserRepository) as T
            modelClass.isAssignableFrom(CourseViewModel::class.java) -> CourseViewModel(repository as UserRepository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(repository as UserRepository) as T
            modelClass.isAssignableFrom(
                com.game.app.containers.course.models.CourseViewModel::class.java
            ) -> com.game.app.containers.course.models.CourseViewModel(repository as UserRepository) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }
}