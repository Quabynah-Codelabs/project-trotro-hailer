package dev.trotrohailer.driver.earnings

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import dev.trotrohailer.driver.R

class EarningsFragment : Fragment() {

    companion object {
        fun newInstance() = EarningsFragment()
    }

    private lateinit var viewModel: EarningsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.earnings_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EarningsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
