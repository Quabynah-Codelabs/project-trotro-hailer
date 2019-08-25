package dev.trotrohailer.passenger.ui.home

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dev.trotrohailer.passenger.R
import dev.trotrohailer.shared.base.BaseTrackingActivity
import dev.trotrohailer.shared.datasource.PassengerRepository
import dev.trotrohailer.shared.util.Constants
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class MapsActivity : BaseTrackingActivity(), OnMapReadyCallback {
    private val repo by inject<PassengerRepository>(named(Constants.PASSENGERS))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        
    }

    override fun onMapReady(googleMap: GoogleMap) {

    }
}
