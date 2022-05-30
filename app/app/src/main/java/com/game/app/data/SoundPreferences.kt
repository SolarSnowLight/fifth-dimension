package com.game.app.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SoundPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    private val dataStore : DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.SOUND_DIALOG_STORE
    )

    val sounds : Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_SOUND]
        }

    suspend fun saveSounds(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_SOUND] = data
        }
    }

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_SOUND = preferencesKey<String>(DataKeyConstants.KEY_DIALOG_SOUND)
    }
}