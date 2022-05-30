package com.game.app.data.room.daos.category

import androidx.lifecycle.LiveData
import androidx.room.*
import com.game.app.data.room.daos.BaseDao
import com.game.app.data.room.models.category.CategoryEntity
import com.game.app.data.room.models.category.CategorySubcategoryEntity
import com.game.app.data.room.models.category.SubcategoryEntity

@Dao
interface CategoryDao : BaseDao<CategoryEntity> {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<CategoryEntity>>

    @Query("DELETE FROM categories")
    suspend fun clearAll()
}