package com.game.app.data.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.game.app.data.room.models.category.CategoryEntity

@Dao
interface BaseDao<T> {
    @Insert
    suspend fun insert(
        element: T
    )

    @Insert
    suspend fun insertAll(
        elements: List<T>
    )

    @Update
    suspend fun update(
        element: T
    )

    @Update
    suspend fun updateAll(
        elements: List<T>
    )

    @Delete
    suspend fun delete(
        element: T
    )

    @Delete
    suspend fun deleteAll(
        elements: List<T>
    )
}