package dev.trotrohailer.passenger.ui.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dev.trotrohailer.shared.data.Driver
import dev.trotrohailer.shared.datasource.DriverRepository
import dev.trotrohailer.shared.result.Response
import dev.trotrohailer.shared.result.succeeded
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class DriverViewModelFactory(private val driverRepository: DriverRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DriverViewModel(driverRepository) as T
    }
}

class DriverViewModel(private val driverRepository: DriverRepository) : ViewModel() {

    fun getAllDrivers(refresh: Boolean = false): MutableList<Driver> {
        val drivers = mutableListOf<Driver>()
        viewModelScope.launch {
            val response = driverRepository.getUsers(refresh)
            if (response.succeeded) {
                drivers.addAll((response as Response.Success).data ?: mutableListOf())
            }
        }
        return drivers
    }

    fun getAllDriversForDestination(
        destination: LatLng,
        refresh: Boolean = false
    ): MutableList<Driver> {
        val drivers = mutableListOf<Driver>()

        // todo: query drivers based on location provided
        viewModelScope.launch {
            val response = driverRepository.getUsers(refresh)
            if (response.succeeded) {
                drivers.addAll((response as Response.Success).data ?: mutableListOf())
            }
        }
        return drivers
    }

}
