package dev.trotrohailer.passenger.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import dev.trotrohailer.passenger.R
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
        viewModel.passenger.observe(viewLifecycleOwner, Observer { passenger ->
            binding.user = passenger
        })
        addTextWatchers()
    }

    private fun addTextWatchers() {
        binding.editUsername.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) return@addTextChangedListener
            binding.user?.name = text.toString().trim()
        }

        binding.editPhone.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) return@addTextChangedListener
            binding.user?.phone = text.toString().trim()
        }

        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.payment_methods)
        )
        binding.editPayment.apply {
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            setAdapter(adapter)
        }
        binding.editPayment.onItemSelectedListener = object : DropDownItemSelectedListener {
            override fun onItemSelected(item: String) {
                // todo: Save this settings somewhere
            }
        }
    }

}

interface DropDownItemSelectedListener : AdapterView.OnItemSelectedListener {
    fun onItemSelected(item: String)

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        onItemSelected(p0?.getItemAtPosition(p2).toString())
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        /* DO Nothing*/
    }

}
