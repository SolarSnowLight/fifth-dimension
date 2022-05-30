package com.game.app.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CourseFilesPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    private val dataStore : DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.COURSE_FILES_STORE
    )

    val soundsImages: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_SOUND_IMG]
        }

    val soundsFile: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_SOUND_FILES]
        }

    suspend fun saveSoundsImages(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_SOUND_IMG] = data
        }
    }

    suspend fun saveSoundFiles(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_SOUND_FILES] = data
        }
    }

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_SOUND_IMG = preferencesKey<String>(DataKeyConstants.KEY_SOUND_IMG)
        private val KEY_SOUND_FILES = preferencesKey<String>(DataKeyConstants.KEY_SOUND_FILES)
    }
}