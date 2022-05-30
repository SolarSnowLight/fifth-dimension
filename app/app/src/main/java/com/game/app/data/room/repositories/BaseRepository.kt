package com.game.app.data.room.repositories

import com.game.app.data.room.daos.BaseDao
import com.game.app.data.room.daos.category.CategoryDao
import com.game.app.data.room.models.category.CategoryEntity

abstract class BaseRepository<T>(private val dao: BaseDao<T>) {
    suspend fun insert(
        element: T
    ){
        dao.insert(element)
    }

    suspend fun insertAll(
        elements: List<T>
    ){
        dao.insertAll(elements)
    }

    suspend fun update(
        element: T
    ){
        dao.update(element)
    }

    suspend fun updateAll(
        elements: List<T>
    ){
        dao.updateAll(elements)
    }

    suspend fun delete(
        element: T
    ){
        dao.delete(element)
    }

    suspend fun deleteAll(
        elements: List<T>
    ){
        dao.deleteAll(elements)
    }
}