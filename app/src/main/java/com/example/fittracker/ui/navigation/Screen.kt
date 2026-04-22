package com.example.fittracker.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Steps : Screen("steps")
    object WorkoutList : Screen("workout_list")
    object WorkoutRecord : Screen("workout_record")
    object WorkoutDetail : Screen("workout_detail/{activityId}") {
        fun createRoute(activityId: Long) = "workout_detail/$activityId"
    }
    object Nutrition : Screen("nutrition")
    object Settings : Screen("settings")
}
