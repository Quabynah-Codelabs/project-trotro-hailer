package dev.trotrohailer.passenger.ui.trip

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dev.trotrohailer.passenger.BuildConfig.*
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.RequestTripFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.passenger.util.toast
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.location.metrics.MapApi
import dev.trotrohailer.shared.util.visible
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import java.util.*
import dev.trotrohailer.shared.R as sharedR

class RequestTripFragment : MainNavigationFragment() {

    private lateinit var binding: RequestTripFragmentBinding
    private lateinit var viewModel: TripViewModel

    private val geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RequestTripFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TripViewModel::class.java)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Cancel request")
                setMessage("Do you wish to cancel your request for a TroTro heading towards your destination?")
                setPositiveButton("Yes, cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    findNavController().popBackStack()
                }
                setNegativeButton("Continue") { dialogInterface, _ -> dialogInterface.dismiss() }
                setCancelable(DEBUG)
                show()
            }
        }

        // Init map
        binding.map.onCreate(savedInstanceState)

        // Get arguments from previous fragment
        val dropoff = arguments?.get("extra_dropoff") as? LatLng
        val pickup = arguments?.get("extra_pickup") as? LatLng

        // get addresses
        debugger("Pickup: $pickup")
        debugger("DropOff: $dropoff")

        val snackbar =
            Snackbar.make(
                binding.container,
                "Calculating distance and duration. Please wait...",
                Snackbar.LENGTH_INDEFINITE
            )

        // Setup UI
        if (pickup != null && dropoff != null) {
            snackbar.show()
            ioScope.launch {

                try {
                    // Get pickup address
                    val address = geocoder.getFromLocation(
                        pickup.latitude,
                        pickup.longitude,
                        1
                    )[0]
                    debugger("Fare: ${address.thoroughfare}")
                    val pickupAddress: String = address.thoroughfare


                    // Get drop off address
                    val dropOffAddress: String = geocoder.getFromLocation(
                        dropoff.latitude,
                        dropoff.longitude,
                        1
                    )[0].thoroughfare

                    // todo: debugging
                    debugger("Pickup address: $pickupAddress")
                    debugger("DropOff address: $dropOffAddress")

                    // Get metrics for distance and duration
                    val mapApi: MapApi = get()

                    // Get distance and duration metrics
                    val mapResult = mapApi.getDistanceForDrivingAsync(
                        origin = "${pickup.latitude},${pickup.longitude}",
                        destination = "${dropoff.latitude}, ${dropoff.longitude}"
                    ).await()
                    if (mapResult.routes.isNotEmpty()) {
                        val distance = mapResult.routes[0].legs[0].distance
                        val duration = mapResult.routes[0].legs[0].duration

                        // todo: debugging
                        debugger("Distance: ${distance.text}")
                        debugger("Duration: ${duration.text}")

                        uiScope.launch {
                            TransitionManager.beginDelayedTransition(binding.container)
                            binding.bottomLayout.visible()
                            snackbar.dismiss()

                            binding.map.getMapAsync { map ->
                                with(map) {
                                    setMapStyle(
                                        MapStyleOptions.loadRawResourceStyle(
                                            requireContext(),
                                            R.raw.mapstyle_uberx
                                        )
                                    )

                                    // Add pickup location
                                    addMarker(
                                        MarkerOptions()
                                            .title(getString(R.string.pickup))
                                            .position(pickup)
                                            .snippet(pickupAddress)
                                            .icon(BitmapDescriptorFactory.fromResource(sharedR.drawable.iconsource_marker))
                                    ).showInfoWindow()

                                    // Add dropoff location
                                    addMarker(
                                        MarkerOptions()
                                            .title(getString(R.string.dropoff))
                                            .position(dropoff)
                                            .snippet(dropOffAddress)
                                            .icon(BitmapDescriptorFactory.fromResource(sharedR.drawable.icondestination_marker))
                                    ).showInfoWindow()

                                    // Move camera to drop off location
                                    animateCamera(
                                        CameraUpdateFactory.newLatLngBounds(
                                            LatLngBounds(pickup, dropoff), 100
                                        ), 550, null
                                    )

                                    // Add bounds for camera target
                                    val bounds: LatLngBounds = LatLngBounds.builder()
                                        .include(dropoff)
                                        .include(pickup)
                                        .include(MAP_VIEWPORT_BOUND_NE)
                                        .include(MAP_VIEWPORT_BOUND_SW)
                                        .build()
                                    map.setLatLngBoundsForCameraTarget(bounds)

                                    // Draw polyline to link locations
                                    addPolyline(
                                        PolylineOptions()
                                            .addAll(mutableListOf(pickup, dropoff))
                                            .color(R.color.warm_blue).startCap(ButtCap())
                                            .endCap(SquareCap())
                                    )

                                    uiSettings.apply {
                                        isCompassEnabled = true
                                        isMapToolbarEnabled = true
                                        isMyLocationButtonEnabled = true
                                        isRotateGesturesEnabled = true
                                        isScrollGesturesEnabled = true
                                        isTiltGesturesEnabled = true
                                        isZoomControlsEnabled = true
                                        isZoomGesturesEnabled = true
                                        isBuildingsEnabled = false
                                        isTrafficEnabled = true
                                        isMyLocationEnabled = true
                                    }
                                }
                            }

                            // Get bottom sheet
                            with(binding) {
                                tvDistance.text = distance.text
                                tvTime.text = duration.text
                                tvPrice.text = getPriceForTrip(distance.value, duration.value)

                                tvPickupLocationText.text = pickupAddress
                                tvDropoffLocationText.text = dropOffAddress
                                btBookRide.setOnClickListener {
                                    findNavController().navigate(
                                        R.id.navigation_find_driver, bundleOf(
                                            Pair("extra_destination", dropoff),
                                            Pair("extra_destination_address", dropOffAddress),
                                            Pair("extra_pickup", pickup),
                                            Pair("extra_pickup_address", pickupAddress)
                                        )
                                    )
                                }
                            }
                        }

                    } else {
                        //
                        Snackbar.make(
                            binding.bottomLayout,
                            "Cannot get distance and duration.",
                            Snackbar.LENGTH_LONG
                        ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                            override fun onDismissed(
                                transientBottomBar: Snackbar?,
                                event: Int
                            ) {
                                //
                                findNavController().popBackStack()
                            }
                        })
                            .show()

                    }
                } catch (e: Exception) {
                    debugger(e.localizedMessage)
                    Snackbar.make(
                        binding.bottomLayout,
                        "Cannot get distance and duration.",
                        Snackbar.LENGTH_LONG
                    )
                        .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                try {
                                    findNavController().popBackStack()
                                } catch (e: Exception) {
                                    debugger(e.localizedMessage)
                                }
                            }
                        })
                        .show()
                }
            }
        } else {
            toast("Unable to setup your routes")
            findNavController().popBackStack()
        }
    }

    private fun getPriceForTrip(distance: Int, duration: Int): CharSequence? =
        "GHS ${distance.div(duration)}"

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroy() {
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

    /* override fun onStart() {
         super.onStart()
         binding.map.onStart()
     }

     override fun onStop() {
         super.onStop()
         binding.map.onStop()
     }*/

}
