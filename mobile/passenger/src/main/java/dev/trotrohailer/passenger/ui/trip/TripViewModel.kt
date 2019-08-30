package dev.trotrohailer.passenger.ui.trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import dev.trotrohailer.shared.util.Constants
import dev.trotrohailer.shared.util.availableDrivers
import org.imperiumlabs.geofirestore.GeoFirestore

class TripViewModel : ViewModel() {
    val isLoading: LiveData<Boolean>
    val swipeRefreshing: LiveData<Boolean>

    init {
        isLoading = MutableLiveData<Boolean>()
        swipeRefreshing = MutableLiveData<Boolean>()
    }

    fun onSwipeRefresh() {
        // todo
    }

    fun getDriversNearBy(
        geoPoint: GeoPoint,
        callback: GeoFirestore.SingleGeoQueryDataEventCallback
    ) {
        GeoFirestore(FirebaseFirestore.getInstance().availableDrivers()).getAtLocation(
            geoPoint,
            0.9,
            callback
        )
    }
}
