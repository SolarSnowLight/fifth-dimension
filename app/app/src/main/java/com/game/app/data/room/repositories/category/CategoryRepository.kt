package com.game.app.data.room.repositories.category

import com.game.app.data.room.daos.category.CategoryDao
import com.game.app.data.room.models.category.CategoryEntity
import com.game.app.data.room.repositories.BaseRepository

class CategoryRepository(private val dao: CategoryDao) : BaseRepository<CategoryEntity>(dao) {
    val categories = dao.getAllCategories()

    suspend fun clearAll(){
        dao.clearAll()
    }
}