package com.example.fittracker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "food_entries",
    foreignKeys = [
        ForeignKey(
            entity = Food::class,
            parentColumns = ["id"],
            childColumns = ["food_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("date_iso"), Index("food_id")]
)
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "food_id")
    val foodId: Long,

    @ColumnInfo(name = "date_iso")
    val dateIso: String,               // "2025-03-15"

    @ColumnInfo(name = "meal_type")
    val mealType: String,              // "breakfast", "lunch", "dinner", "snack"

    @ColumnInfo(name = "amount_grams")
    val amountGrams: Float,

    @ColumnInfo(name = "logged_at_ms")
    val loggedAtMs: Long
)
