package com.game.app.data.room.daos.category

import androidx.lifecycle.LiveData
import androidx.room.*
import com.game.app.data.room.daos.BaseDao
import com.game.app.data.room.models.category.CategoryEntity
import com.game.app.data.room.models.category.CategorySubcategoryEntity

@Dao
interface CategorySubcategoryDao : BaseDao<CategorySubcategoryEntity> {
    @Query("SELECT * FROM categories_subcategories")
    fun getAllCategoriesSubcategories(): LiveData<List<CategorySubcategoryEntity>>

    @Query("DELETE FROM categories_subcategories")
    suspend fun clearAll()
}