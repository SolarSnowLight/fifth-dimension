package com.game.app.data.room.models.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "subcategories")
data class SubcategoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    @NotNull
    val id: Long?,

    @ColumnInfo(name = "title")
    @NotNull
    var title: String?,

    @ColumnInfo(name = "description")
    @NotNull
    var description: String?
)