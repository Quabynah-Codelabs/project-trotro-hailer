package dev.trotrohailer.driver

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import dev.trotrohailer.driver.auth.AuthActivity
import dev.trotrohailer.driver.main.HomeActivity
import dev.trotrohailer.shared.base.BaseActivity
import dev.trotrohailer.shared.util.intentTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class LauncherActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth: FirebaseAuth = get()
        ioScope.launch {
            delay(850)
            intentTo(
                if (auth.currentUser == null) AuthActivity::class.java else HomeActivity::class.java,
                true
            )
        }
    }
}
