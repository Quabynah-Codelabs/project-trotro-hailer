package dev.trotrohailer.passenger.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.trotrohailer.passenger.databinding.ProfileFragmentBinding
import dev.trotrohailer.passenger.ui.settings.SettingsViewModel
import dev.trotrohailer.passenger.util.MainNavigationFragment
import org.koin.android.ext.android.inject

class ProfileFragment : MainNavigationFragment() {
    private lateinit var binding: ProfileFragmentBinding
    private val viewModel by inject<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

    }

    /*companion object {
        fun newInstance() = ProfileFragment()
    }*/

}
