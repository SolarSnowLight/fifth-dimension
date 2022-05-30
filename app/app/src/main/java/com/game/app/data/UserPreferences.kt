package com.game.app.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/*
* Класс для взаимодействия с локальными пользовательскими данными
* */
class UserPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    // Создание DataStore (локального хранилища)
    private val dataStore : DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.MAIN_STORE
    )

    // Сохранение данных в виде асинхронного потока
    val authData : Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_AUTH]
        }

    // Асинхронная функция сохранения авторизационных данных
    suspend fun saveAuthData(authData: String){
        // Сохранение данных
        dataStore.edit { preferences ->
            preferences[KEY_AUTH] = authData
        }
    }

    val userInfoData : Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_INFO]
        }

    suspend fun saveUserInfoData(userInfo: String){
        dataStore.edit { preferences ->
            preferences[KEY_USER_INFO] = userInfo
        }
    }

    // Очистка локального хранилища
    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_AUTH = preferencesKey<String>(DataKeyConstants.KEY_AUTH)
        private val KEY_USER_INFO = preferencesKey<String>(DataKeyConstants.KEY_USER_INFO)
    }
}