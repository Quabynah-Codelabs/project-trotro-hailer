package dev.trotrohailer.passenger.ui.home

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseAuth
import com.hypertrack.sdk.HyperTrack
import dev.trotrohailer.passenger.R
import dev.trotrohailer.shared.base.BaseTrackingActivity
import dev.trotrohailer.shared.datasource.PassengerRepository
import dev.trotrohailer.shared.result.Response
import dev.trotrohailer.shared.result.succeeded
import dev.trotrohailer.shared.util.Constants
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.location.MyLocationGoogleMap
import dev.trotrohailer.shared.util.mapToPassenger
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class MapsActivity : BaseTrackingActivity(), OnMapReadyCallback {
    private val repo by inject<PassengerRepository>(named(Constants.PASSENGERS))
    private val auth by inject<FirebaseAuth>()
    private val customMap by lazy { MyLocationGoogleMap(this) }

    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        HyperTrack.addNotificationIconsAndTitle(
            dev.trotrohailer.shared.R.drawable.fake_car,
            dev.trotrohailer.shared.R.drawable.group,
            getString(
                dev.trotrohailer.shared.R.string.default_app_name_passenger
            ),
            "You are currently visible to all drivers in your vicinity"
        )
        fetchAndSaveUser()

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

    }

    private fun fetchAndSaveUser() {
        try {
            ioScope.launch {
                val response = repo.getUser(auth.currentUser?.uid!!, false)
                if (response.succeeded) {
                    debugger("Current user: ${(response as Response.Success).data}")
                } else {
                    repo.saveUser(auth.currentUser!!.mapToPassenger())
                }
            }
        } catch (e: Exception) {
            debugger(e.localizedMessage)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.backup_map_style))
        customMap.addTo(map)
        customMap.moveToMyLocation(map)
    }

    override fun onDestroy() {
        if (map != null) this.customMap.removeFrom(map)
        super.onDestroy()
    }
}
