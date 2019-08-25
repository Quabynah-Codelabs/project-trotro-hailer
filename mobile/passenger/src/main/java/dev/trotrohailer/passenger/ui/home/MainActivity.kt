package dev.trotrohailer.passenger.ui.home

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.hypertrack.sdk.HyperTrack
import dev.trotrohailer.passenger.R
import dev.trotrohailer.shared.base.BaseTrackingActivity
import dev.trotrohailer.shared.databinding.HeaderViewBinding
import dev.trotrohailer.shared.datasource.PassengerRepository
import dev.trotrohailer.shared.result.Response
import dev.trotrohailer.shared.util.Constants
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.mapToPassenger
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class MainActivity : BaseTrackingActivity() {
    private val repo by inject<PassengerRepository>(named(Constants.PASSENGERS))
    private val auth by inject<FirebaseAuth>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        HyperTrack.addNotificationIconsAndTitle(
            dev.trotrohailer.shared.R.drawable.fake_car,
            dev.trotrohailer.shared.R.drawable.group,
            getString(
                dev.trotrohailer.shared.R.string.default_app_name_passenger
            ),
            "You are currently visible to all drivers in your vicinity"
        )
        fetchAndSaveUser()
    }

    private fun fetchAndSaveUser() {
        try {
            ioScope.launch {
                when (val response =
                    repo.getUser(auth.currentUser?.uid!!, hasNetworkConnection())) {
                    is Response.Success -> {
                        debugger("Current user: ${response.data}")
                        uiScope.launch {
                            HeaderViewBinding.inflate(layoutInflater).passenger = response.data
                        }
                        if (response.data == null) repo.saveUser(auth.currentUser!!.mapToPassenger())
                    }

                    is Response.Error -> {
                        debugger("Error loading avatar: ${response.e.localizedMessage}")
                        repo.saveUser(auth.currentUser!!.mapToPassenger())
                    }

                    is Response.Loading -> {
                        debugger("Loading user avatar")
                    }
                }
            }
        } catch (e: Exception) {
            debugger(e.localizedMessage)
        }
    }


}
