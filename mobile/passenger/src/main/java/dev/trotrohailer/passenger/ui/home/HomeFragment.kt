package dev.trotrohailer.passenger.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.trotrohailer.passenger.BuildConfig
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.HomeFragmentBinding
import dev.trotrohailer.passenger.ui.settings.SettingsViewModel
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.passenger.util.toast
import dev.trotrohailer.shared.data.Coordinate
import dev.trotrohailer.shared.util.gone
import dev.trotrohailer.shared.util.invisible
import dev.trotrohailer.shared.util.location.MyLocationGoogleMap
import dev.trotrohailer.shared.util.toLatLng
import dev.trotrohailer.shared.util.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        ioScope.launch {
            delay(850)
            uiScope.launch {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle("Welcome back to ${getString(R.string.default_app_name_passenger)}")
                    setMessage("To request a driver, tap the \"Request driver\" button below and click anywhere on the map to set as your destination. It's that easy. Give it a try!")
                    setPositiveButton("Okay, got it!") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    show()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.map.onStop()
    }

    // Locations
    private var pickupLocation: LatLng = BuildConfig.MAP_VIEWPORT_BOUND_SW
    private var dropoffLocation: LatLng = BuildConfig.MAP_VIEWPORT_BOUND_NE
    private var marker: MarkerOptions? = null

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
            setupMap()
        else
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), RC_PERMS
            )
    }

    private fun setupMap() {
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
                        location?.toLatLng(),
                        15.0f
                    ),
                    850,
                    null
                )
                true
            }
        }

        // Get view model for current user
        val viewModel: SettingsViewModel = get()

        // Get last location and update variables
        val lastLocation = customMap.lastLocation
        if (lastLocation != null) {
            pickupLocation = lastLocation.toLatLng()
            dropoffLocation = LatLng(lastLocation.latitude + 0.0001, lastLocation.longitude)

            // Get passenger and update coordinates property
            val passenger = viewModel.passenger.value.apply {
                this?.coordinate = Coordinate(lastLocation.latitude, lastLocation.longitude)
            }
            if (passenger != null) viewModel.saveUser(passenger)
        }

        // Show pickup button and allow selection of pickup position
        binding.confirmPickup.visible()
        binding.confirmPickup.setOnClickListener {
            binding.confirmPickup.invisible()
            binding.confirmDropOff.visible()

            if (marker == null) {
                marker = MarkerOptions()
                    .position(dropoffLocation)
                    .draggable(true)
                    .alpha(1.0f)
                    .icon(BitmapDescriptorFactory.fromResource(dev.trotrohailer.shared.R.drawable.icondestination_marker))
            }

            map?.addMarker(marker)
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    marker?.position,
                    16.0f
                )
            )
        }

        map?.setOnMapClickListener { latLng ->
            map?.clear()
            marker?.position(latLng)
            dropoffLocation = latLng
            if (marker != null) map?.addMarker(marker)
        }

        // Show drop-off button and allow selection of pickup position
        binding.confirmDropOff.setOnClickListener {
            // Hide dropoff button
            binding.confirmDropOff.gone()

            // Confirm pickup location
            binding.confirmPickup.apply {
                visible()
                text = "Find a driver for this trip"
                setOnClickListener {
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            setupMap()
    }

    companion object {
        private const val RC_PERMS = 88
    }

}
