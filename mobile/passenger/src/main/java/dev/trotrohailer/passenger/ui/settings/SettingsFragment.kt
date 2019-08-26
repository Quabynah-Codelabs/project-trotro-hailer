package dev.trotrohailer.passenger.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.util.MainNavigationFragment
import org.koin.android.ext.android.inject

class SettingsFragment : MainNavigationFragment() {
    private val viewModel by inject<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}
