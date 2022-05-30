package com.game.app.models.category

import com.google.gson.annotations.SerializedName

data class CategoryModel(
    @SerializedName("categories" ) var categories : ArrayList<CategoryItemModel> = arrayListOf()
)
