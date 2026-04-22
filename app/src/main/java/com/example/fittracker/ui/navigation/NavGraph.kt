package com.example.fittracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fittracker.ui.screens.home.HomeScreen
import com.example.fittracker.ui.screens.nutrition.NutritionScreen
import com.example.fittracker.ui.screens.settings.SettingsScreen
import com.example.fittracker.ui.screens.steps.StepsScreen
import com.example.fittracker.ui.screens.workout.WorkoutDetailScreen
import com.example.fittracker.ui.screens.workout.WorkoutListScreen
import com.example.fittracker.ui.screens.workout.WorkoutRecordScreen

@Composable
fun FitTrackerNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSteps = { navController.navigate(Screen.Steps.route) },
                onNavigateToWorkouts = { navController.navigate(Screen.WorkoutList.route) },
                onNavigateToNutrition = { navController.navigate(Screen.Nutrition.route) }
            )
        }
        composable(Screen.Steps.route) {
            StepsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.WorkoutList.route) {
            WorkoutListScreen(
                onStartWorkout = { navController.navigate(Screen.WorkoutRecord.route) },
                onOpenWorkout = { id -> navController.navigate(Screen.WorkoutDetail.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.WorkoutRecord.route) {
            WorkoutRecordScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(navArgument("activityId") { type = NavType.LongType })
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getLong("activityId") ?: return@composable
            WorkoutDetailScreen(
                activityId = activityId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Nutrition.route) {
            NutritionScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
