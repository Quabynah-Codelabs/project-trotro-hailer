package dev.trotrohailer.passenger.ui.trip

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import dev.trotrohailer.passenger.databinding.RequestRideBottomSheetBinding
import dev.trotrohailer.passenger.databinding.RequestTripFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.shared.util.debugger
import kotlinx.coroutines.launch
import java.util.*

class RequestTripFragment : MainNavigationFragment() {
    private lateinit var binding: RequestTripFragmentBinding
    private lateinit var bottomSheetBinding: RequestRideBottomSheetBinding
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

        bottomSheetBinding =
            RequestRideBottomSheetBinding.inflate(layoutInflater, binding.container, false)

        val dropoff = arguments?.get("extra_dropoff") as? LatLng
        val pickup = arguments?.get("extra_pickup") as? LatLng

        // get addresses
        debugger("Pickup: $pickup")
        debugger("DropOff: $dropoff")

        if (pickup != null && dropoff != null) {
            ioScope.launch {
                // Get pickup address
                val pickupAddress: String = geocoder.getFromLocation(
                    pickup.latitude,
                    pickup.longitude,
                    2
                )[0].getAddressLine(0)

                // Get drop off address
                val dropOffAddress: String = geocoder.getFromLocation(
                    dropoff.latitude,
                    dropoff.longitude,
                    2
                )[0].getAddressLine(0)

                debugger("Pickup address: $pickupAddress")
                debugger("DropOff address: $dropOffAddress")

                uiScope.launch {
                    bottomSheetBinding.apply {
                        tvPickupLocationText.text = pickupAddress
                        tvDropoffLocationText.text = dropOffAddress
                    }
                }
            }
        }
    }

}
