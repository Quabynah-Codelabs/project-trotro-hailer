package dev.trotrohailer.passenger

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import dev.trotrohailer.passenger.ui.auth.AuthActivity
import dev.trotrohailer.shared.base.BaseActivity
import dev.trotrohailer.shared.datasource.PassengerRepository
import dev.trotrohailer.shared.util.Constants
import dev.trotrohailer.shared.util.debugger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

/**
 * launcher screen
 * No layout needed
 */
class LauncherActivity : BaseActivity() {
    private val repo by inject<PassengerRepository>(named(Constants.PASSENGERS))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        // Firebase auth instance
        val auth: FirebaseAuth = get()
        debugger("Repo for passenger created as: ${repo::class.java.name}")

        ioScope.launch {
            debugger("Current user: ${auth.currentUser}")
        }

        ioScope.launch {
            delay(850)
            startActivity(Intent(this@LauncherActivity, AuthActivity::class.java))
            finishAffinity()
        }
    }

}