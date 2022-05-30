package com.game.app.data.room

import com.game.app.data.room.models.category.CategoryEntity
import com.game.app.data.room.models.category.CategorySubcategoryEntity
import com.game.app.data.room.models.category.SubcategoryEntity
import com.game.app.data.room.repositories.category.CategoryRepository
import com.game.app.data.room.repositories.category.CategorySubcategoryRepository
import com.game.app.data.room.repositories.category.SubcategoryRepository
import com.game.app.repositories.BaseRepository

class AppDatabaseRepository(
    private val categoryRepository: CategoryRepository,
    private val categorySubcategoryRepository: CategorySubcategoryRepository,
    private val subcategoryRepository: SubcategoryRepository
) : BaseRepository() {

    val categories = categoryRepository.categories
    val subcategories = subcategoryRepository.subcategories
    val categoriesSubcategories = categorySubcategoryRepository.categoriesSubcategories

    suspend fun insertCategory(
        category: CategoryEntity?,
        subcategory: SubcategoryEntity?,
        categorySubcategory: CategorySubcategoryEntity?
    ){
        if(category != null){
            categoryRepository.insert(category)
        }

        if(subcategory != null){
            subcategoryRepository.insert(subcategory)
        }

        if(categorySubcategory != null){
            categorySubcategoryRepository.insert(categorySubcategory)
        }
    }

    suspend fun insertCategories(
        category: List<CategoryEntity>?,
        subcategory: List<SubcategoryEntity>?,
        categorySubcategory: List<CategorySubcategoryEntity>?
    ){
        if(category != null){
            categoryRepository.insertAll(category)
        }

        if(subcategory != null){
            subcategoryRepository.insertAll(subcategory)
        }

        if(categorySubcategory != null){
            categorySubcategoryRepository.insertAll(categorySubcategory)
        }
    }

    suspend fun updateCategory(
        category: CategoryEntity?,
        subcategory: SubcategoryEntity?,
        categorySubcategory: CategorySubcategoryEntity?
    ){
        if(category != null){
            categoryRepository.update(category)
        }

        if(subcategory != null){
            subcategoryRepository.update(subcategory)
        }

        if(categorySubcategory != null){
            categorySubcategoryRepository.update(categorySubcategory)
        }
    }

}