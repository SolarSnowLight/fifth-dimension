package com.game.app.models.category

import com.google.gson.annotations.SerializedName

data class SubCategoryItemModel(
    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("title"       ) var title       : String? = null,
    @SerializedName("description" ) var description : String? = null
)
