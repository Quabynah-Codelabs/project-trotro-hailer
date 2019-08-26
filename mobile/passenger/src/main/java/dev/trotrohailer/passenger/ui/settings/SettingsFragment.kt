package dev.trotrohailer.passenger.ui.settings

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.SettingsFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment
import org.koin.android.ext.android.inject

class SettingsFragment : MainNavigationFragment() {
    private val viewModel by inject<SettingsViewModel>()
    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.passenger.observe(this, Observer {
            binding.viewModel = viewModel
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_edit -> {
                findNavController().navigate(R.id.navigation_profile)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
