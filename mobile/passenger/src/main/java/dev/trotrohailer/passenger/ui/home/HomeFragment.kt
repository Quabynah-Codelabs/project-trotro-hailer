package dev.trotrohailer.passenger.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MapStyleOptions
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.HomeFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment
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
        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.map.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        binding.map.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.map.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.mapstyle_uberx
            )
        )
        customMap.addTo(map)
        customMap.moveToMyLocation(map)
    }

    override fun onDestroy() {
        if (map != null) this.customMap.removeFrom(map)
        super.onDestroy()
    }
}
