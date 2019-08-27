package dev.trotrohailer.passenger.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import dev.trotrohailer.passenger.databinding.SettingsFragmentBinding
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.passenger.util.prefs.PaymentPrefs
import dev.trotrohailer.shared.util.debugger
import org.koin.android.ext.android.inject

class SettingsFragment : MainNavigationFragment() {
    private val viewModel by inject<SettingsViewModel>()
    private lateinit var binding: SettingsFragmentBinding
    private val prefs by inject<PaymentPrefs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.passenger.observe(viewLifecycleOwner, Observer { passenger ->
            binding.viewModel = viewModel
            debugger("Observing passenger from settings page: ${passenger.id}")
        })

        prefs.paymentMethod.observe(viewLifecycleOwner, Observer { method ->
            debugger("Payment method: $method")
            binding.prefs = prefs
        })
    }
}
