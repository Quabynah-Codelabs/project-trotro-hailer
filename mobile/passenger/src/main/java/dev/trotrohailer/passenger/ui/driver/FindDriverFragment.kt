package dev.trotrohailer.passenger.ui.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.FindDriverFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.shared.util.debugger
import org.koin.android.ext.android.get

class FindDriverFragment : MainNavigationFragment() {
    private lateinit var binding: FindDriverFragmentBinding
    private lateinit var adapter: BusDriversAdapter
    private var shouldShowPrompt: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FindDriverFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel: DriverViewModel = get()

        // Destination details
        val desLocation = arguments?.get("extra_destination") as? LatLng
        val desAddress = arguments?.get("extra_destination_address") as? String

        // Pickup details
        val picLocation = arguments?.get("extra_pickup") as? LatLng
        val picAddress = arguments?.get("extra_pickup_address") as? String

        debugger("Pickup address: $picAddress")

        // Add different colors for the swipe refresh action
        val colorResIds: IntArray = requireContext().resources.getIntArray(R.array.swipe_refresh)
        binding.swipeRefresh.setColorSchemeColors(*colorResIds)
        binding.swipeRefresh.setOnRefreshListener {
            val drivers = viewModel.getAllDrivers(true)
            binding.swipeRefresh.isRefreshing = false
            adapter.addDrivers(drivers)
        }

        adapter = BusDriversAdapter(requireContext())
        with(binding.driversList) {
            adapter = this@FindDriverFragment.adapter
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }

        // Get all drivers and add to adapter
        val drivers = viewModel.getAllDrivers(true)
        adapter.addDrivers(drivers)

        if (drivers.isEmpty() && shouldShowPrompt) {
            shouldShowPrompt = false
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Heads up")
                setMessage("You can swipe from the top of the screen to refresh this list. There may be an issue with your internet connection or maybe there are no drivers for your destination:\n\n$desAddress\n")
                setPositiveButton("Ok, got it") { dialogInterface, _ -> dialogInterface.dismiss() }
                show()
            }
        }
    }


}
