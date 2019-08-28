package dev.trotrohailer.passenger.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.TripFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TripFragment : MainNavigationFragment() {
    private lateinit var binding: TripFragmentBinding
    private lateinit var viewModel: TripViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TripFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Get view model
        viewModel = ViewModelProvider(this).get(TripViewModel::class.java)

        // Add different colors for the swipe refresh action
        val colorResIds: IntArray = requireContext().resources.getIntArray(R.array.swipe_refresh)
        binding.swipeRefresh.setColorSchemeColors(*colorResIds)
        binding.swipeRefresh.setOnRefreshListener {
            // todo: get trips for current user
            ioScope.launch {
                delay(850)
                uiScope.launch {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }


}
