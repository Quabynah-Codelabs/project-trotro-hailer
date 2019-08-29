package dev.trotrohailer.passenger.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.SettingsFragmentBinding
import dev.trotrohailer.passenger.ui.auth.AuthActivity
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.passenger.util.prefs.PaymentPrefs
import dev.trotrohailer.shared.util.debugger
import org.koin.android.ext.android.get
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

        // Get firebase auth instance
        val auth: FirebaseAuth = get()

        // Add different colors for the swipe refresh action
        val colorResIds: IntArray = requireContext().resources.getIntArray(R.array.swipe_refresh)
        binding.swipeRefresh.setColorSchemeColors(*colorResIds)

        // Allow swipe to refresh user information
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getUser(auth.currentUser?.uid!!, true)
                .observe(viewLifecycleOwner, Observer { user ->
                    debugger("Refreshed user: $user")
                    binding.viewModel = viewModel
                    binding.swipeRefresh.isRefreshing = false
                })
        }

        binding.logout.setOnClickListener {
            viewModel.logout(it)
            prefs.removePayment()
            startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
            requireActivity().finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        val auth: FirebaseAuth = get()
        // get live updates of current user
        viewModel.getUser(auth.currentUser?.uid!!, false)
            .observe(viewLifecycleOwner, Observer {
                binding.viewModel = viewModel
            })

        // get payment method for this user
        prefs.paymentMethod.observe(viewLifecycleOwner, Observer { method ->
            debugger("Payment method: $method")
            binding.prefs = prefs
        })
    }
}
