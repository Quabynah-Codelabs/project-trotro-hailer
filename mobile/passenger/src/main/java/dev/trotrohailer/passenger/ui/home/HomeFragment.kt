package dev.trotrohailer.passenger.ui.home

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dev.trotrohailer.passenger.BuildConfig
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.HomeFragmentBinding
import dev.trotrohailer.passenger.ui.settings.SettingsViewModel
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.passenger.util.toast
import dev.trotrohailer.shared.data.Coordinate
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.invisible
import dev.trotrohailer.shared.util.location.MyLocationGoogleMap
import dev.trotrohailer.shared.util.toLatLng
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.map.onCreate(savedInstanceState)
        //binding.map.getMapAsync(this)
        val fragment = childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
        fragment?.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_search) {
            toast("Hello search")
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
        if (map != null) {
            customMap.addTo(map)
            customMap.moveToMyLocation(map)
        }
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
    private var pickupLocation: LatLng = BuildConfig.MAP_VIEWPORT_BOUND_SW
    private var dropoffLocation: LatLng = BuildConfig.MAP_VIEWPORT_BOUND_NE

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.setOnMapClickListener { latLng ->
            debugger("Location is: $latLng")
        }
        map?.isTrafficEnabled = true
        customMap.addTo(map)
        customMap.moveToMyLocation(map)
        with(map) {
            this?.isMyLocationEnabled = true
            
            map?.uiSettings.apply {
                this?.isCompassEnabled = true
                this?.isMapToolbarEnabled = true
                this?.isMyLocationButtonEnabled = true
                this?.isRotateGesturesEnabled = true
                this?.isScrollGesturesEnabled = true
                this?.isTiltGesturesEnabled = true
                this?.isZoomControlsEnabled = true
                this?.isZoomGesturesEnabled = true
            }

            this?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.mapstyle_uberx
                )
            )

            this?.setOnMyLocationButtonClickListener {
                val location = map?.myLocation
                this.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        location!!.toLatLng(),
                        BuildConfig.MAP_CAMERA_FOCUS_ZOOM
                    ),
                    850,
                    null
                )
                true
            }

            val bounds: LatLngBounds = LatLngBounds.builder()
                .include(customMap.lastLocation.toLatLng())
                .include(BuildConfig.MAP_VIEWPORT_BOUND_NE)
                .include(BuildConfig.MAP_VIEWPORT_BOUND_SW)
                .build()
            this?.setLatLngBoundsForCameraTarget(bounds)

        }

        // Get view model for current user
        val viewModel: SettingsViewModel = get()

        // Get last location and update variables
        val lastLocation = customMap.lastLocation
        debugger("My last location: ${lastLocation?.toLatLng()}")
        if (lastLocation != null) {
            pickupLocation = lastLocation.toLatLng()
            dropoffLocation = LatLng(lastLocation.latitude + 0.11, lastLocation.longitude + 0.21)

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
                    .icon(BitmapDescriptorFactory.fromResource(dev.trotrohailer.shared.R.drawable.iconsource_marker))
            )
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pickupLocation,
                    BuildConfig.MAP_CAMERA_FOCUS_ZOOM
                )
            )
            binding.confirmPickup.invisible()
            binding.confirmDropOff.visible()
        }

        // Show drop-off button and allow selection of pickup position
        binding.confirmDropOff.setOnClickListener {
            googleMap?.addMarker(
                MarkerOptions()
                    .position(dropoffLocation)
                    .icon(BitmapDescriptorFactory.fromResource(dev.trotrohailer.shared.R.drawable.icondestination_marker))
            )
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    dropoffLocation,
                    BuildConfig.MAP_CAMERA_FOCUS_ZOOM
                )
            )

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
