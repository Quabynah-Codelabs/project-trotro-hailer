package dev.trotrohailer.driver.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.GeoPoint
import dev.trotrohailer.driver.BuildConfig
import dev.trotrohailer.driver.R
import dev.trotrohailer.shared.util.debugger
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getPassengerRequestsAsync(
            GeoPoint(
                BuildConfig.MAP_VIEWPORT_BOUND_NE.latitude,
                BuildConfig.MAP_VIEWPORT_BOUND_NE.longitude
            )
        ) { docId, geoPoint ->
            debugger("Getting location of passengers with request")

        }
    }

}
