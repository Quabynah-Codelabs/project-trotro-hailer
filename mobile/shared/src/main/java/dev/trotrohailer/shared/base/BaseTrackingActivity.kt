package dev.trotrohailer.shared.base

import android.os.Bundle
import com.hypertrack.sdk.HyperTrack
import dev.trotrohailer.shared.BuildConfig

open class BaseTrackingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize HyperTrack for activity
        HyperTrack.initialize(this, BuildConfig.HYPER_TRACK_PUB_KEY)
        if (BuildConfig.DEBUG) {
            HyperTrack.enableDebugLogging()
            HyperTrack.enableMockLocation()
        }
    }

}