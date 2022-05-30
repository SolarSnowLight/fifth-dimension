package com.game.app.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StartPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    private val dataStore : DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.START_STORE
    )

    val firstOpen : Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_FIRST_OPEN]
        }

    suspend fun saveFirstOpen(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_FIRST_OPEN] = data
        }
    }

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_FIRST_OPEN = preferencesKey<String>(DataKeyConstants.KEY_FIRST_OPEN)
    }
}