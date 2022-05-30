package com.game.app.data.room.models.category

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(
    tableName = "categories_subcategories",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categories_id"],
        onDelete = CASCADE
    ), ForeignKey(
        entity = SubcategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["subcategories_id"],
        onDelete = CASCADE
    )]
)
data class CategorySubcategoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    @NotNull
    val id: Long?,

    @ColumnInfo(name = "categories_id")
    @NotNull
    var categoriesId: Long?,

    @ColumnInfo(name = "subcategories_id")
    @NotNull
    var subcategoriesId: Long?,

    @ColumnInfo(name = "categories_subcategories_id")
    @Nullable
    var categoriesSubcategoriesId: Long?
)