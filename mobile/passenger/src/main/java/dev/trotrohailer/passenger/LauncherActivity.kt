package dev.trotrohailer.passenger

import android.content.Intent
import android.os.Bundle
import dev.trotrohailer.passenger.ui.home.MapsActivity
import dev.trotrohailer.shared.base.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * launcher screen
 * No layout needed
 */
class LauncherActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ioScope.launch {
            delay(850)
            startActivity(Intent(this@LauncherActivity, MapsActivity::class.java))
            finishAffinity()
        }
    }

}