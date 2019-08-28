package dev.trotrohailer.passenger.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.HomeFragmentBinding
import dev.trotrohailer.passenger.ui.settings.SettingsViewModel
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.shared.data.Coordinate
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.invisible
import dev.trotrohailer.shared.util.location.MyLocationGoogleMap
import dev.trotrohailer.shared.util.visible
import org.koin.android.ext.android.get

class HomeFragment : MainNavigationFragment(), OnMapReadyCallback {
    private lateinit var binding: HomeFragmentBinding
    private val customMap by lazy { MyLocationGoogleMap(requireContext()) }
    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /* val mapFragment =
             childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
         mapFragment?.getMapAsync(this)*/
        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroy() {
        if (map != null) this.customMap.removeFrom(map)
        super.onDestroy()
        binding.map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.map.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        binding.map.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.map.onStop()
    }

    // Locations
    private var pickupLocation: LatLng = LatLng(5.556, -0.231)
    private var dropoffLocation: LatLng = LatLng(5.556, -0.231)

    override fun onMapReady(googleMap: GoogleMap?) {
        debugger("Starting maps...")
        map = googleMap
        map?.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.mapstyle_uberx
            )
        )
        customMap.addTo(map)
        customMap.moveToMyLocation(map)

        // Get view model for current user
        val viewModel: SettingsViewModel = get()

        // Get last location and update variables
        val lastLocation = customMap.lastLocation
        debugger("My last location: ${lastLocation?.latitude}, ${lastLocation?.longitude}")
        if (lastLocation != null) {
            pickupLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
            dropoffLocation = LatLng(lastLocation.latitude + 0.01, lastLocation.longitude + 0.02)

            // Get passenger and update coordinates property
            val passenger = viewModel.passenger.value.apply {
                this?.coordinate = Coordinate(lastLocation.latitude, lastLocation.longitude)
            }
            if (passenger != null) viewModel.saveUser(passenger)
        }

        // Show pickup button and allow selection of pickup position
        binding.confirmPickup.visible()
        binding.confirmPickup.setOnClickListener {
            googleMap?.clear()
            googleMap?.addMarker(
                MarkerOptions()
                    .position(pickupLocation)
                    .icon(BitmapDescriptorFactory.fromResource(dev.trotrohailer.shared.R.drawable.iconmap_marker))
            )
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 15.0f))
            binding.confirmPickup.invisible()
            binding.confirmDropOff.visible()
        }

        // Show drop-off button and allow selection of pickup position
        binding.confirmDropOff.setOnClickListener {
            googleMap?.addMarker(
                MarkerOptions()
                    .position(dropoffLocation)
                    .icon(BitmapDescriptorFactory.fromResource(dev.trotrohailer.shared.R.drawable.iconmap_marker))
            )
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(dropoffLocation, 15.0f))

            // Navigation
            findNavController().navigate(
                R.id.navigation_request_trips, bundleOf(
                    Pair("extra_pickup", pickupLocation),
                    Pair("extra_dropoff", dropoffLocation)
                )
            )
        }
    }

}
