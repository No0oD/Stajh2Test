package com.example.stajh2test.ui.states

import android.app.Application
import timber.log.Timber

/**
 * Initializes Timber logging in the application
 */
class TimberConfig {
    companion object {
        fun init(application: Application) {
            if (application.applicationInfo.packageName.contains("debug")) {
                Timber.plant(Timber.DebugTree())
            } else {
                // You could plant a crash reporting tree here for production
                // e.g., Timber.plant(CrashReportingTree())
            }
        }
    }
}

// Example usage in your Application class:
/*
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TimberConfig.init(this)
    }
}
*/