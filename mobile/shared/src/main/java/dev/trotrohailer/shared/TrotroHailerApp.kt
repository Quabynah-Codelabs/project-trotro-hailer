package dev.trotrohailer.shared

import android.app.Application
import com.google.firebase.FirebaseApp
import dev.trotrohailer.shared.util.debugger

class TrotroHailerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase Init
        FirebaseApp.initializeApp(this@TrotroHailerApp)
            .apply { debugger("Firebase SDK: ${this?.name}") }
    }

}