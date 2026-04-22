package com.example.fittracker.data.repository

import com.example.fittracker.data.db.dao.FoodDao
import com.example.fittracker.data.db.entity.Food
import com.example.fittracker.data.db.entity.FoodEntry
import kotlinx.coroutines.flow.Flow

class NutritionRepository(private val foodDao: FoodDao) {

    fun searchFoodsFlow(query: String): Flow<List<Food>> = foodDao.searchFoodsFlow(query)

    fun getRecentFoodsFlow(): Flow<List<Food>> = foodDao.getRecentFoodsFlow()

    suspend fun getFoodByBarcode(barcode: String): Food? = foodDao.getFoodByBarcode(barcode)

    suspend fun saveFood(food: Food): Long = foodDao.insertFood(food)

    suspend fun updateFood(food: Food) = foodDao.updateFood(food)

    suspend fun deleteFood(food: Food) = foodDao.deleteFood(food)

    suspend fun markFoodUsed(foodId: Long) =
        foodDao.updateLastUsed(foodId, System.currentTimeMillis())

    fun getEntriesForDateFlow(dateIso: String): Flow<List<FoodEntry>> =
        foodDao.getEntriesForDateFlow(dateIso)

    suspend fun addFoodEntry(entry: FoodEntry): Long = foodDao.insertEntry(entry)

    suspend fun deleteFoodEntry(entry: FoodEntry) = foodDao.deleteEntry(entry)
}
