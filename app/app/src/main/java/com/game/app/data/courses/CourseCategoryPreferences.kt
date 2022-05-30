package com.game.app.data.courses

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.game.app.constants.data.store.DataKeyConstants
import com.game.app.constants.data.store.DataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Global storage for all courses with category
class CourseCategoryPreferences(
    context: Context,
    categoryId: String?
) {
    private val appContext = context.applicationContext
    private val keyCourse = preferencesKey<String>(DataKeyConstants.KEY_COURSE_HEADER + categoryId)

    private val dataStore: DataStore<Preferences> = appContext.createDataStore(
        name = DataStoreConstants.COURSE_STORE_HEADER + categoryId
    )

    val courses: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[keyCourse]
        }

    suspend fun saveCourses(data: String) {
        dataStore.edit { preferences ->
            preferences[keyCourse] = data
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}