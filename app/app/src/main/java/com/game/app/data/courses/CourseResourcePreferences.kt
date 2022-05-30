package com.game.app.data.courses

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ! BAD CODE !
class CourseResourcePreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    private val dataStore : DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.COURSE_RESOURCE_STORE
    )

    // Информация о всех курсах
    val courses: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_COURSE]
        }

    // Соответствие изображения каждому курсу
    val coursesImg: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_COURSE_IMG]
        }

    suspend fun saveCourses(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_COURSE] = data
        }
    }

    suspend fun saveCoursesImg(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_COURSE_IMG] = data
        }
    }

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_COURSE = preferencesKey<String>(DataKeyConstants.KEY_COURSE_RESOURCE)
        private val KEY_COURSE_IMG = preferencesKey<String>(DataKeyConstants.KEY_COURSE_RESOURCE_IMG)
    }
}