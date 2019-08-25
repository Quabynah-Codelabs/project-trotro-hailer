package dev.trotrohailer.shared

import android.app.Application
import com.google.firebase.FirebaseApp
import dev.trotrohailer.shared.BuildConfig.DEBUG
import dev.trotrohailer.shared.injection.loadModules
import dev.trotrohailer.shared.util.debugger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class TrotroHailerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase Init
        FirebaseApp.initializeApp(this@TrotroHailerApp)
            .apply { debugger("Firebase SDK: ${this?.name}") }

        startKoin {
            androidContext(this@TrotroHailerApp)
            androidLogger(if (DEBUG) Level.DEBUG else Level.INFO)
            modules(loadModules())
        }

    }

}