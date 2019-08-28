package dev.trotrohailer.passenger.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dev.trotrohailer.passenger.databinding.RequestTripFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment

class RequestTripFragment : MainNavigationFragment() {
    private lateinit var binding: RequestTripFragmentBinding
    private lateinit var viewModel: TripViewModel

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


    }

}
