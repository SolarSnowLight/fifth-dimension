package com.game.app.data.room.daos.category

import androidx.lifecycle.LiveData
import androidx.room.*
import com.game.app.data.room.daos.BaseDao
import com.game.app.data.room.models.category.SubcategoryEntity

@Dao
interface SubcategoryDao : BaseDao<SubcategoryEntity> {
    @Query("SELECT * FROM subcategories")
    fun getAllSubCategories(): LiveData<List<SubcategoryEntity>>

    @Query("DELETE FROM subcategories")
    suspend fun clearAll()
}