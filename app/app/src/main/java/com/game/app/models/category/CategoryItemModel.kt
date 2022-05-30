package com.game.app.models.category

import com.google.gson.annotations.SerializedName

data class CategoryItemModel(
    @SerializedName("id"             ) var id            : Int?                     = null,
    @SerializedName("title"          ) var title         : String?                  = null,
    @SerializedName("description"    ) var description   : String?                  = null,
    @SerializedName("sub_categories" ) var subCategories : ArrayList<SubCategoryItemModel> = arrayListOf()
)
