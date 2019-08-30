package dev.trotrohailer.driver.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import dev.trotrohailer.shared.util.availableDrivers
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.passengerRequests
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener

class HomeViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(firestore, auth) as T
    }
}


class HomeViewModel(
    private val firestore: FirebaseFirestore, private val auth: FirebaseAuth
) : ViewModel() {
    private val availableDrivers: GeoFirestore = GeoFirestore(firestore.availableDrivers())
    private val passengerReqs: GeoFirestore = GeoFirestore(firestore.passengerRequests())

    fun toggleVisibility(isVisible: Boolean = true, geoPoint: GeoPoint? = null) {
        if (isVisible && geoPoint != null) {
            debugger("Visibility on")
            availableDrivers.setLocation(auth.currentUser?.uid!!, geoPoint)
        } else {
            debugger("Visibility off")
            availableDrivers.removeLocation(auth.currentUser?.uid!!)
        }
    }

    fun getPassengerRequestsAsync(
        geoPoint: GeoPoint,
        callback: MultiParamsCallback<String?, GeoPoint?>
    ) {
        passengerReqs.queryAtLocation(geoPoint, 0.9)
            .addGeoQueryEventListener(object : GeoQueryEventListener {
                override fun onGeoQueryError(exception: Exception) {
                    callback(null, null)
                }

                override fun onGeoQueryReady() {
                    debugger("Geoquery is ready")
                }

                override fun onKeyEntered(documentID: String, location: GeoPoint) {
                    debugger("onKeyEntered: $documentID")
                    callback(documentID, geoPoint)
                }

                override fun onKeyExited(documentID: String) {

                }

                override fun onKeyMoved(documentID: String, location: GeoPoint) {
                    debugger("onKeyMoved: $documentID")
                    callback(documentID, geoPoint)
                }
            })
    }

    fun getMyLocation(callback: Callback<GeoPoint?>) {
        availableDrivers.getLocation(
            auth.currentUser?.uid!!,
            object : GeoFirestore.LocationCallback {
                override fun onComplete(location: GeoPoint?, exception: Exception?) {
                    callback(location)
                }
            })
    }
}

typealias Callback<R> = (R) -> Unit
typealias MultiParamsCallback<R, T> = (R, T) -> Unit
