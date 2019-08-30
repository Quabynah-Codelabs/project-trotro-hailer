package dev.trotrohailer.driver.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.firestore.GeoPoint
import dev.trotrohailer.driver.BuildConfig
import dev.trotrohailer.driver.R
import dev.trotrohailer.driver.databinding.HomeFragmentBinding
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.location.MyLocationGoogleMap
import dev.trotrohailer.shared.util.toGeoPoint
import dev.trotrohailer.shared.util.toLatLng
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val customMap by lazy { MyLocationGoogleMap(requireContext()) }
    private lateinit var binding: HomeFragmentBinding
    private val prefs by lazy {
        requireContext().getSharedPreferences(
            "driver_status",
            Context.MODE_PRIVATE
        )
    }
    private var isDriverVisible: Boolean = true

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            map = googleMap
            customMap.addTo(map)
            customMap.moveToMyLocation(googleMap)

            with(map) {
                this.isMyLocationEnabled = true

                map.uiSettings.apply {
                    this?.isCompassEnabled = true
                    this?.isMapToolbarEnabled = true
                    this?.isMyLocationButtonEnabled = true
                    this?.isRotateGesturesEnabled = true
                    this?.isScrollGesturesEnabled = true
                    this?.isTiltGesturesEnabled = true
                }

                this.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.mapstyle_uberx
                    )
                )

                this.setOnMyLocationButtonClickListener {
                    val location = map.myLocation
                    this.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            location?.toLatLng(),
                            15.0f
                        ),
                        850,
                        null
                    )
                    true
                }
            }
        }
    }

    private val viewModel by viewModel<HomeViewModel>()
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.getPassengerRequestsAsync(
            GeoPoint(
                BuildConfig.MAP_VIEWPORT_BOUND_NE.latitude,
                BuildConfig.MAP_VIEWPORT_BOUND_NE.longitude
            )
        ) { docId, geoPoint ->
            debugger("Getting location of passengers with request")

        }

        isDriverVisible = prefs.getBoolean("key_driver_status", false)

        with(binding.fabStatus)
        {
            setIconResource(if (isDriverVisible) R.drawable.ic_visible else R.drawable.ic_invisible)
            text = if (isDriverVisible) "You are live" else "You are offline"
        }

        // Add action to toggle driver's visibility with the FAB
        binding.fabStatus.setOnClickListener {
            if (isDriverVisible) {
                isDriverVisible = false
                binding.fabStatus.text = "You are offline"
                binding.fabStatus.setIconResource(R.drawable.ic_invisible)
            } else {
                isDriverVisible = true
                binding.fabStatus.text = "You are live"
                binding.fabStatus.setIconResource(R.drawable.ic_visible)
            }
            updatePrefs(isDriverVisible)
        }
    }

    // Update visibility
    private fun updatePrefs(visible: Boolean) {
        viewModel.toggleVisibility(visible, customMap.lastLocation?.toGeoPoint())
        prefs.edit {
            putBoolean("key_driver_status", visible)
            apply()
        }
    }

}
