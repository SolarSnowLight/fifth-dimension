package com.game.app.data.content

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteCoursePreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    private val dataStore : DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.COURSE_NOTE_STORE
    )

    // Информация о всех курсах
    val courses: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_COURSE_NOTE]
        }

    suspend fun saveCourses(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_COURSE_NOTE] = data
        }
    }

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_COURSE_NOTE = preferencesKey<String>(DataKeyConstants.KEY_COURSE_NOTE)
    }
}