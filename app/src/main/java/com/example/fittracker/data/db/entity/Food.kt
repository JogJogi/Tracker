package com.example.fittracker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "foods",
    indices = [Index("barcode"), Index("source")]
)
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "brand")
    val brand: String = "",

    @ColumnInfo(name = "barcode")
    val barcode: String? = null,

    // Macros per 100g
    @ColumnInfo(name = "calories_per_100g")
    val caloriesPer100g: Float,

    @ColumnInfo(name = "protein_g_per_100g")
    val proteinGPer100g: Float = 0f,

    @ColumnInfo(name = "carbs_g_per_100g")
    val carbsGPer100g: Float = 0f,

    @ColumnInfo(name = "fat_g_per_100g")
    val fatGPer100g: Float = 0f,

    @ColumnInfo(name = "fiber_g_per_100g")
    val fiberGPer100g: Float = 0f,

    // "usda", "openfoodfacts", "custom"
    @ColumnInfo(name = "source")
    val source: String,

    @ColumnInfo(name = "source_id")
    val sourceId: String? = null,

    @ColumnInfo(name = "last_used_ms")
    val lastUsedMs: Long? = null
)
