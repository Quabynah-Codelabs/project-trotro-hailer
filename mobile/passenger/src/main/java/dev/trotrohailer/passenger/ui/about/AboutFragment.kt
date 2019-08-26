package dev.trotrohailer.passenger.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.trotrohailer.passenger.databinding.AboutFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment

class AboutFragment : MainNavigationFragment() {
    private lateinit var binding: AboutFragmentBinding
    //private lateinit var viewModel: AboutViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AboutFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
