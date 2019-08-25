package dev.trotrohailer.passenger

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import dev.trotrohailer.passenger.ui.auth.AuthActivity
import dev.trotrohailer.passenger.ui.home.MapsActivity
import dev.trotrohailer.shared.base.BaseActivity
import dev.trotrohailer.shared.util.intentTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

/**
 * launcher screen
 * No layout needed
 */
class LauncherActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        // Firebase auth instance
        val auth: FirebaseAuth = get()
        ioScope.launch {
            delay(850)
            intentTo(
                if (auth.currentUser == null) AuthActivity::class.java else MapsActivity::class.java,
                true
            )
        }
    }

}