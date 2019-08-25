package dev.trotrohailer.shared

import android.app.Application
import com.google.firebase.FirebaseApp

class TrotroHailerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Firebase Init
        FirebaseApp.initializeApp(this@TrotroHailerApp)
    }

}