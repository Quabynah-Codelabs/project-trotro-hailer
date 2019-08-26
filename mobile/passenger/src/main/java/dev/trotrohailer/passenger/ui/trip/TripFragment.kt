package dev.trotrohailer.passenger.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import dev.trotrohailer.passenger.databinding.TripFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment

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
        viewModel = ViewModelProviders.of(this).get(TripViewModel::class.java)

    }

}
