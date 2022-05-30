package com.game.app.data.room.repositories.category

import com.game.app.data.room.daos.category.SubcategoryDao
import com.game.app.data.room.models.category.SubcategoryEntity
import com.game.app.data.room.repositories.BaseRepository

class SubcategoryRepository(private val dao: SubcategoryDao) : BaseRepository<SubcategoryEntity>(dao) {
    val subcategories = dao.getAllSubCategories()

    suspend fun clearAll(){
        dao.clearAll()
    }
}