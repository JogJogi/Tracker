package com.example.fittracker.di

import androidx.room.Room
import com.example.fittracker.data.db.AppDatabase
import com.example.fittracker.data.export.CsvExporter
import com.example.fittracker.data.export.GpxExporter
import com.example.fittracker.data.export.JsonExporter
import com.example.fittracker.data.preferences.AppPreferences
import com.example.fittracker.data.repository.ActivityRepository
import com.example.fittracker.data.repository.BodyMeasurementRepository
import com.example.fittracker.data.repository.NutritionRepository
import com.example.fittracker.data.repository.StepsRepository
import com.example.fittracker.ui.screens.home.HomeViewModel
import com.example.fittracker.ui.screens.nutrition.NutritionViewModel
import com.example.fittracker.ui.screens.settings.SettingsViewModel
import com.example.fittracker.ui.screens.steps.StepsViewModel
import com.example.fittracker.ui.screens.workout.WorkoutListViewModel
import com.example.fittracker.ui.screens.workout.WorkoutViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(*AppDatabase.MIGRATIONS)
            .build()
    }

    // DAOs
    single { get<AppDatabase>().activityDao() }
    single { get<AppDatabase>().trackPointDao() }
    single { get<AppDatabase>().stepsDao() }
    single { get<AppDatabase>().heartRateDao() }
    single { get<AppDatabase>().foodDao() }
    single { get<AppDatabase>().bodyMeasurementDao() }

    // Preferences
    single { AppPreferences(androidContext()) }

    // Repositories
    single { ActivityRepository(get(), get()) }
    single { StepsRepository(get()) }
    single { NutritionRepository(get()) }
    single { BodyMeasurementRepository(get()) }

    // Exporters
    factory { GpxExporter() }
    factory { CsvExporter() }
    factory { JsonExporter() }

    // ViewModels
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { StepsViewModel(get(), get()) }
    viewModel { WorkoutViewModel() }
    viewModel { WorkoutListViewModel(get()) }
    viewModel { NutritionViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
