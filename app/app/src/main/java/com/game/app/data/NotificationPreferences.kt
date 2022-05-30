package com.game.app.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    private val dataStore : DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.NOTIFICATION_STORE
    )

    val notification : Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_NOTIFICATION]
        }

    suspend fun saveNotification(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATION] = data
        }
    }

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_NOTIFICATION = preferencesKey<String>(DataKeyConstants.NOTIFICATION_KEY)
    }
}