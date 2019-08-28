package dev.trotrohailer.passenger.ui.coupons

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import dev.trotrohailer.passenger.R

class CouponsFragment : Fragment() {

    companion object {
        fun newInstance() = CouponsFragment()
    }

    private lateinit var viewModel: CouponsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.coupons_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CouponsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
