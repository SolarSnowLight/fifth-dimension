package com.game.app.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CoursesPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    private val dataStore : DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.COURSES_STORE
    )

    // Информация о всех курсах
    val courses: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_COURSES]
        }

    // Соответствие изображения каждому курсу
    val coursesImg: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_COURSES_IMG]
        }

    suspend fun saveCourses(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_COURSES] = data
        }
    }

    suspend fun saveCoursesImg(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_COURSES_IMG] = data
        }
    }

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_COURSES = preferencesKey<String>(DataKeyConstants.KEY_COURSES)
        private val KEY_COURSES_IMG = preferencesKey<String>(DataKeyConstants.KEY_COURSES_IMG)
    }
}