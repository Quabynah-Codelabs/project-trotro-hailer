package dev.trotrohailer.shared.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hypertrack.sdk.HyperTrack
import dev.trotrohailer.shared.BuildConfig

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize HyperTrack for activity
        HyperTrack.initialize(this, BuildConfig.HYPER_TRACK_PUB_KEY)
    }
}