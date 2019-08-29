package dev.trotrohailer.shared.util.location.live

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dev.trotrohailer.shared.util.debugger


class LiveLocationUpdate(private val host: AppCompatActivity) : LifecycleOwner, LocationListener,
    GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    override fun onConnected(p0: Bundle?) {
        debugger("API client connected")
    }

    override fun onConnectionSuspended(p0: Int) {
        debugger("API client suspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        debugger("Failed to connect to API client")
    }

    private val _liveLocation = MutableLiveData<Location>()
    val lastLocation: LiveData<Location> = _liveLocation
    private var apiClient: GoogleApiClient? = null


    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            _liveLocation.value = location
        }
    }

    init {
        val locationRequest = LocationRequest()
        with(locationRequest) {
            fastestInterval = 1000
            interval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        buildApiClient()
        LocationServices.getFusedLocationProviderClient(host).lastLocation.addOnCompleteListener(
            host
        ) {
            if (it.isSuccessful) {
                _liveLocation.value = it.result
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    @Synchronized
    //@RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun buildApiClient() {
        apiClient = GoogleApiClient.Builder(host.applicationContext)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        apiClient?.connect()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun disconnect() = apiClient?.disconnect()


    override fun getLifecycle(): Lifecycle = host.lifecycle
}
