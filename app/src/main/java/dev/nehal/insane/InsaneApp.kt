package dev.nehal.insane

import android.app.Application
import dev.nehal.insane.shared.AppPreferences

class InsaneApp:Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}