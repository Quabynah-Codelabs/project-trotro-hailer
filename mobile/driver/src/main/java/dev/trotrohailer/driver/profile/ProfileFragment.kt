package dev.trotrohailer.driver.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import dev.trotrohailer.driver.R
import dev.trotrohailer.driver.databinding.ProfileFragmentBinding
import dev.trotrohailer.driver.main.HomeViewModel
import dev.trotrohailer.driver.util.MainNavigationFragment
import dev.trotrohailer.driver.viewmodel.DriverViewModel
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.location.GpsMyLocationProvider
import dev.trotrohailer.shared.util.toLatLng
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : MainNavigationFragment() {
    private lateinit var binding: ProfileFragmentBinding
    private val viewModel by viewModel<DriverViewModel>()
    private val homeViewModel by viewModel<HomeViewModel>()

    private val db by inject<FirebaseFirestore>()
    private val auth by inject<FirebaseAuth>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Get live driver information
        // Add different colors for the swipe refresh action
        val colorResIds: IntArray = requireContext().resources.getIntArray(R.array.swipe_refresh)
        binding.swipeRefresh.setColorSchemeColors(*colorResIds)

        // Allow swipe to refresh user information
        binding.swipeRefresh.setOnRefreshListener {
            // Remove old observers
            viewModel.driver.removeObservers(viewLifecycleOwner)

            // get current user's information
            viewModel.getUser(auth.currentUser?.uid!!, true)
                .observe(viewLifecycleOwner, Observer { user ->
                    debugger("Refreshed user: $user")
                    binding.viewModel = viewModel
                    binding.swipeRefresh.isRefreshing = false
                })
        }

        // Get driver information
        viewModel.driver.observe(viewLifecycleOwner, Observer {
            binding.viewModel = viewModel
        })

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
            getActiveLocationUpdates()
        else requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), RC_PERM
        )
    }

    private fun getActiveLocationUpdates() {
        // Toggle visibility to active by default
        with(GpsMyLocationProvider(requireContext())) {
            val myLastLocation = this.lastKnownLocation?.toLatLng()
            if (myLastLocation != null) {
                homeViewModel.toggleVisibility(
                    true,
                    GeoPoint(myLastLocation.latitude, myLastLocation.longitude)
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_PERM && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            getActiveLocationUpdates()
    }

    companion object {
        private const val RC_PERM = 77
    }

}
