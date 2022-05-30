package com.game.app.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CurrentListenAudioPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    private val dataStore : DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.SOUND_CURRENT_LISTEN
    )

    // Информация о текущем прослушиваемом аудиофайле
    val currentSound: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CURRENT_LISTEN]
        }

    // Сохранение аудиофайла
    suspend fun saveCurrentSound(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_CURRENT_LISTEN] = data
        }
    }

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_CURRENT_LISTEN = preferencesKey<String>(DataKeyConstants.KEY_CURRENT_LISTEN)
    }
}