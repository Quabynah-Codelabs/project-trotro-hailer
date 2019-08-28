package dev.trotrohailer.passenger.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.hypertrack.sdk.HyperTrack
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.HomeFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.shared.BuildConfig
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.location.MyLocationGoogleMap

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
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

    }

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

        // Initialize HyperTrack here
        HyperTrack.initialize(requireActivity(), BuildConfig.HYPER_TRACK_PUB_KEY)
        HyperTrack.enableMockLocation()
        HyperTrack.tripMarker(
            hashMapOf<String, Any?>(
                Pair("user", "Quabynah Bilson"),
                Pair("previousOwner", mutableListOf<String>()),
                Pair("price", 12.99)
            )
        )
    }

    override fun onDestroy() {
        if (map != null) this.customMap.removeFrom(map)
        super.onDestroy()
    }
}
