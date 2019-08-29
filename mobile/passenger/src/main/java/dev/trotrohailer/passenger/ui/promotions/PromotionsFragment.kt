package dev.trotrohailer.passenger.ui.promotions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.shared.base.BaseActivity
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.location.live.LiveLocationUpdate

class PromotionsFragment : MainNavigationFragment() {

    private lateinit var viewModel: PromotionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.promotions_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PromotionsViewModel::class.java)

        //
        LiveLocationUpdate(requireActivity() as BaseActivity).lastLocation.observe(
            viewLifecycleOwner,
            Observer { location ->
                debugger("Current location: $location")
            })

    }

}
