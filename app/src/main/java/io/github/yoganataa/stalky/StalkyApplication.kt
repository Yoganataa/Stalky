package io.github.yoganataa.stalky

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StalkyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any required components
    }
}