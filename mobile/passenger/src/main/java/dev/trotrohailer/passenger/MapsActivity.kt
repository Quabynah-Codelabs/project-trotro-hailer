package dev.trotrohailer.passenger

import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.hypertrack.sdk.HyperTrack
import com.hypertrack.sdk.TrackingInitDelegate
import com.hypertrack.sdk.TrackingInitError
import dev.trotrohailer.shared.base.BaseActivity

class MapsActivity : BaseActivity(), OnMapReadyCallback, TrackingInitDelegate {
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_uberx))

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f))

        //HyperTrack.startTracking(true, this)
    }



    override fun onSuccess() {
        println("TroTro ==> Tracking started")
    }

    override fun onError(error: TrackingInitError) {
        println("TroTro ==> Error while tracking: ${error.localizedMessage}")
    }
}
