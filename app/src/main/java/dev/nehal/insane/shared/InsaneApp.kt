package dev.nehal.insane.shared

import android.app.Application

class InsaneApp:Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}