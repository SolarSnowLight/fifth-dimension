package com.game.app.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.game.app.data.room.daos.category.CategoryDao
import com.game.app.data.room.daos.category.CategorySubcategoryDao
import com.game.app.data.room.daos.category.SubcategoryDao
import com.game.app.data.room.models.category.CategoryEntity
import com.game.app.data.room.models.category.CategorySubcategoryEntity
import com.game.app.data.room.models.category.SubcategoryEntity

@Database(entities = [
    CategoryEntity::class,
    SubcategoryEntity::class,
    CategorySubcategoryEntity::class
                     ], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // Categories Dao
    abstract val categoryDao: CategoryDao
    abstract val subcategoryDao: SubcategoryDao
    abstract val categorySubcategoryDao: CategorySubcategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "db_app"
                    ).build()
                }

                return instance
            }
        }
    }
}