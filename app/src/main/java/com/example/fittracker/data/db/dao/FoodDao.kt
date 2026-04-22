package com.example.fittracker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fittracker.data.db.entity.Food
import com.example.fittracker.data.db.entity.FoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    // Foods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: Food): Long

    @Update
    suspend fun updateFood(food: Food)

    @Delete
    suspend fun deleteFood(food: Food)

    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getFoodById(id: Long): Food?

    @Query("SELECT * FROM foods WHERE barcode = :barcode LIMIT 1")
    suspend fun getFoodByBarcode(barcode: String): Food?

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' ORDER BY last_used_ms DESC LIMIT 50")
    fun searchFoodsFlow(query: String): Flow<List<Food>>

    @Query("SELECT * FROM foods ORDER BY last_used_ms DESC LIMIT 20")
    fun getRecentFoodsFlow(): Flow<List<Food>>

    @Query("UPDATE foods SET last_used_ms = :timestampMs WHERE id = :foodId")
    suspend fun updateLastUsed(foodId: Long, timestampMs: Long)

    // Food Entries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FoodEntry): Long

    @Delete
    suspend fun deleteEntry(entry: FoodEntry)

    @Query("SELECT * FROM food_entries WHERE date_iso = :dateIso ORDER BY logged_at_ms ASC")
    fun getEntriesForDateFlow(dateIso: String): Flow<List<FoodEntry>>

    @Query("DELETE FROM food_entries WHERE date_iso = :dateIso")
    suspend fun deleteEntriesForDate(dateIso: String)
}
