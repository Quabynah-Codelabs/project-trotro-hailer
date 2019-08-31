package dev.trotrohailer.passenger.ui.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import dev.trotrohailer.passenger.BuildConfig
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.FindDriverFragmentBinding
import dev.trotrohailer.passenger.ui.trip.TripViewModel
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.passenger.util.toast
import dev.trotrohailer.shared.util.availableDrivers
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.passengerRequests
import org.imperiumlabs.geofirestore.GeoFirestore
import org.koin.android.ext.android.get

class FindDriverFragment : MainNavigationFragment() {
    private lateinit var binding: FindDriverFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FindDriverFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel: TripViewModel = get()

        // Destination details
        val desLocation = arguments?.get("extra_destination") as? LatLng
        val desAddress = arguments?.get("extra_destination_address") as? String

        // Pickup details
        val picLocation = arguments?.get("extra_pickup") as? LatLng
        val picAddress = arguments?.get("extra_pickup_address") as? String

        if (desLocation != null && picLocation != null) {
            // Collection reference
            val db: FirebaseFirestore = get()
            val auth: FirebaseAuth = get()

            // Store passenger's current location information in the database
            GeoFirestore(db.passengerRequests()).setLocation(
                auth.currentUser?.uid!!,
                GeoPoint(picLocation.latitude, picLocation.longitude)
            )

            // Get nearby drivers for request
            viewModel.getDriversNearBy(GeoPoint(desLocation.latitude, desLocation.longitude),
                object : GeoFirestore.SingleGeoQueryDataEventCallback {
                    override fun onComplete(
                        documentSnapshots: List<DocumentSnapshot>?,
                        exception: Exception?
                    ) {
                        if (exception != null) {
                            debugger("Could not find drivers around: ${exception.localizedMessage}")
                            MaterialAlertDialogBuilder(requireContext()).apply {
                                setTitle("An error occurred")
                                setMessage("We could not get any drivers heading your way. Please try again later")
                                setPositiveButton("Ok") { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                }
                                setOnDismissListener {
                                    findNavController().popBackStack()
                                }
                                show()
                            }
                            return
                        }

                        if (documentSnapshots != null) {
                            debugger("Drivers found: ${documentSnapshots.size}")
                            if (documentSnapshots.isEmpty()) {
                                // show dialog to user
                               /* MaterialAlertDialogBuilder(requireContext()).apply {
                                    setTitle("Oops...")
                                    setMessage("There are no drivers available in your current location. Please try again later")
                                    setPositiveButton("Okay") { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                        findNavController().popBackStack()
                                    }
                                    show()
                                }*/
                                GeoFirestore(db.availableDrivers()).getLocation(
                                        "Pe2PJcbflPUlZ1QQ95KCJ0Q3VHr1", object : GeoFirestore.LocationCallback {
                                        override fun onComplete(
                                            location: GeoPoint?,
                                            exception: Exception?
                                        ) {
                                            MaterialAlertDialogBuilder(requireContext()).apply {
                                                setTitle("Driver found")
                                                setMessage("You have a driver available. Do you wish to join this TroTro?")
                                                setPositiveButton("Okay") { dialogInterface, _ ->
                                                    dialogInterface.dismiss()
                                                    // todo: join trip
                                                    toast("You ride has been started successfully")
                                                    findNavController().popBackStack()
                                                }
                                                setNegativeButton("No") { dialogInterface, _ ->
                                                    dialogInterface.dismiss()
                                                    findNavController().popBackStack()
                                                }
                                                show()
                                            }
                                        }
                                    })
                            } else {
                                // Driver has been found
                                MaterialAlertDialogBuilder(requireContext()).apply {
                                    setTitle("Driver found")
                                    setMessage("You have a driver available. Do you wish to join this TroTro?")
                                    setPositiveButton("Okay") { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                        // todo: join trip
                                        toast("You ride has been started successfully")
                                        findNavController().navigate(R.id.navigation_home)
                                    }
                                    setNegativeButton("No") { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                        findNavController().popBackStack()
                                    }
                                    show()
                                }
                            }
                        } else {
                            toast("Could not find available drivers at this time")
                            findNavController().popBackStack()
                        }
                    }
                })

            // Back action handler
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle("Cancel request")
                    setMessage("Do you wish to cancel your request for a TroTro heading towards your destination?")
                    setPositiveButton("Yes, cancel") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        with(GeoFirestore(db.passengerRequests())) {
                            removeLocation(auth.currentUser?.uid!!)
                        }
                        findNavController().popBackStack()
                    }
                    setNegativeButton("Continue") { dialogInterface, _ -> dialogInterface.dismiss() }
                    setCancelable(BuildConfig.DEBUG)
                    show()
                }
            }
        }

    }


}
