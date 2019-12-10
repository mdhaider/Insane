package dev.nehal.insane.shared

import android.app.Application
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class InsaneApp:Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
            Timber.plant(Timber.DebugTree())
        }
        AppPreferences.init(this)
        instance = this
    }
    companion object {
        lateinit var instance: InsaneApp
    }
}