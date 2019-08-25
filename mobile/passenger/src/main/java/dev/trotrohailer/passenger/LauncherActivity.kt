package dev.trotrohailer.passenger

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import dev.trotrohailer.passenger.ui.auth.AuthActivity
import dev.trotrohailer.shared.base.BaseActivity
import dev.trotrohailer.shared.util.debugger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * launcher screen
 * No layout needed
 */
class LauncherActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        ioScope.launch {
            debugger("Current user: ${inject<FirebaseAuth>().value.currentUser}")
        }

        ioScope.launch {
            delay(850)
            startActivity(Intent(this@LauncherActivity, AuthActivity::class.java))
            finishAffinity()
        }
    }

}