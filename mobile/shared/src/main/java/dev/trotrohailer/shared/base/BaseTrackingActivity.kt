package dev.trotrohailer.shared.base

import android.os.Bundle
import com.hypertrack.sdk.HyperTrack
import com.hypertrack.sdk.TrackingInitDelegate
import com.hypertrack.sdk.TrackingInitError
import dev.trotrohailer.shared.BuildConfig
import dev.trotrohailer.shared.util.debugger

abstract class BaseTrackingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize HyperTrack for activity
        HyperTrack.initialize(
            this,
            BuildConfig.HYPER_TRACK_PUB_KEY,
            false,
            true,
            object : TrackingInitDelegate {
                override fun onSuccess() {
                    locationServicesEnabled()
                }

                override fun onError(p0: TrackingInitError) {
                    debugger("Error initializing tracking delegate. ${p0.localizedMessage}")
                }
            })
    }

    protected abstract fun locationServicesEnabled()
}