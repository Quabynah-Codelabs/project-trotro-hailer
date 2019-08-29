package dev.trotrohailer.driver.viewmodel

import android.view.View
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import dev.trotrohailer.shared.data.Driver
import dev.trotrohailer.shared.datasource.DriverRepository
import dev.trotrohailer.shared.result.Response
import dev.trotrohailer.shared.result.succeeded
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.toast
import kotlinx.coroutines.launch

class DriverViewModelFactory(
    private val repository: DriverRepository,
    private val auth: FirebaseAuth
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DriverViewModel(repository, auth) as T
    }
}

class DriverViewModel(private val repository: DriverRepository, private val auth: FirebaseAuth) :
    ViewModel() {

    private val _driver = MutableLiveData<Driver>()

    init {
        viewModelScope.launch {
            try {
                val response = repository.getUser(auth.uid!!, false)
                if (response.succeeded) {
                    _driver.postValue((response as Response.Success).data)
                }
            } catch (e: Exception) {
                debugger(e.localizedMessage)
            }
        }
    }

    val driver: LiveData<Driver> = _driver

    fun saveUser(driver: Driver) = viewModelScope.launch {
        repository.saveUser(driver)
    }

    fun getUsers(refresh: Boolean = false): LiveData<MutableList<Driver>> {
        val users = MutableLiveData<MutableList<Driver>>()
        viewModelScope.launch {
            when (val response = repository.getUsers(refresh)) {
                is Response.Success -> {
                    users.postValue(response.data)
                }
                else -> {
                    //
                    users.postValue(mutableListOf())
                }
            }
        }
        return users
    }

    fun getUser(id: String, refresh: Boolean = false): LiveData<Driver> {
        val user = MutableLiveData<Driver>()
        viewModelScope.launch {
            when (val response = repository.getUser(id, refresh)) {
                is Response.Success -> {
                    user.postValue(response.data)
                }
                else -> {
                    //
                    user.postValue(null)
                }
            }
        }
        return user
    }

    fun saveAndExit(view: View, driver: Driver?) {
        debugger("Driver to be saved: $driver")
        viewModelScope.launch {
            if (driver != null) repository.saveUser(driver)
        }
        view.toast("Profile information updated successfully")
        Navigation.findNavController(view).popBackStack()
    }

    fun logout(view: View) = repository.logoutUser(view.context)

}