package com.example.fittracker.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.db.entity.Food
import com.example.fittracker.data.db.entity.FoodEntry
import com.example.fittracker.data.repository.NutritionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class DailyNutrition(
    val caloriesKcal: Float = 0f,
    val proteinG: Float = 0f,
    val carbsG: Float = 0f,
    val fatG: Float = 0f
)

data class NutritionUiState(
    val selectedDate: String = "",
    val entries: List<Pair<FoodEntry, Food?>> = emptyList(),
    val dailyNutrition: DailyNutrition = DailyNutrition(),
    val recentFoods: List<Food> = emptyList(),
    val searchResults: List<Food> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class NutritionViewModel(private val nutritionRepository: NutritionRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
    )
    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<NutritionUiState> = combine(
        _selectedDate,
        _selectedDate.flatMapLatest { date -> nutritionRepository.getEntriesForDateFlow(date) },
        nutritionRepository.getRecentFoodsFlow(),
        _searchQuery.flatMapLatest { q ->
            if (q.isBlank()) nutritionRepository.getRecentFoodsFlow()
            else nutritionRepository.searchFoodsFlow(q)
        }
    ) { date, entries, recentFoods, searchResults ->
        val daily = entries.fold(DailyNutrition()) { acc, entry ->
            // Note: in a real app, look up food to get macros. Here we store summary.
            acc
        }
        NutritionUiState(
            selectedDate = date,
            entries = entries.map { it to null }, // food lookup can be added
            dailyNutrition = daily,
            recentFoods = recentFoods,
            searchResults = searchResults,
            searchQuery = _searchQuery.value,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NutritionUiState(
            selectedDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        )
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun logFood(foodId: Long, amountGrams: Float, mealType: String) {
        viewModelScope.launch {
            nutritionRepository.addFoodEntry(
                FoodEntry(
                    foodId = foodId,
                    dateIso = _selectedDate.value,
                    mealType = mealType,
                    amountGrams = amountGrams,
                    loggedAtMs = System.currentTimeMillis()
                )
            )
            nutritionRepository.markFoodUsed(foodId)
        }
    }

    fun deleteEntry(entry: FoodEntry) {
        viewModelScope.launch { nutritionRepository.deleteFoodEntry(entry) }
    }
}
