package com.example.fittracker

import android.app.Application
import com.example.fittracker.di.appModule
import com.example.fittracker.service.worker.PeriodicStepSyncWorker
import com.example.fittracker.service.worker.WidgetRefreshWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class FitTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)
            androidContext(this@FitTrackerApp)
            modules(appModule)
        }
        PeriodicStepSyncWorker.schedule(this)
        WidgetRefreshWorker.schedule(this)
    }
}
