package com.game.app.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    private val dataStore : DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.CATEGORY_STORE
    )

    val categories : Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CATEGORY]
        }

    suspend fun saveCategories(data: String){
        dataStore.edit { preferences ->
            preferences[KEY_CATEGORY] = data
        }
    }

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_CATEGORY = preferencesKey<String>(DataKeyConstants.KEY_CATEGORY)
    }
}