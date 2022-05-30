package com.game.app.data.room.repositories.category

import com.game.app.data.room.daos.category.CategorySubcategoryDao
import com.game.app.data.room.models.category.CategorySubcategoryEntity
import com.game.app.data.room.repositories.BaseRepository

class CategorySubcategoryRepository(private val dao: CategorySubcategoryDao) : BaseRepository<CategorySubcategoryEntity>(dao) {
    val categoriesSubcategories = dao.getAllCategoriesSubcategories()

    suspend fun clearAll(){
        dao.clearAll()
    }
}