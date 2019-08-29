package dev.trotrohailer.passenger.ui.settings

import android.view.View
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import dev.trotrohailer.passenger.R
import dev.trotrohailer.shared.data.Passenger
import dev.trotrohailer.shared.datasource.PassengerRepository
import dev.trotrohailer.shared.result.Response
import dev.trotrohailer.shared.result.succeeded
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.toast
import kotlinx.coroutines.launch

class SettingsViewModelFactory(
    private val repository: PassengerRepository,
    private val auth: FirebaseAuth
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(repository, auth) as T
    }
}

class SettingsViewModel constructor(
    private val repository: PassengerRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _passenger = MutableLiveData<Passenger>()

    init {
        viewModelScope.launch {
            try {
                val response = repository.getUser(auth.uid!!, false)
                if (response.succeeded) {
                    _passenger.postValue((response as Response.Success).data)
                }
            } catch (e: Exception) {
                debugger(e.localizedMessage)
            }
        }
    }

    val passenger: LiveData<Passenger> = _passenger

    fun saveUser(passenger: Passenger) = viewModelScope.launch {
        repository.saveUser(passenger)
    }

    fun getUsers(refresh: Boolean = false): LiveData<MutableList<Passenger>> {
        val users = MutableLiveData<MutableList<Passenger>>()
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

    fun getUser(id: String, refresh: Boolean = false): LiveData<Passenger> {
        val user = MutableLiveData<Passenger>()
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

    fun addPaymentMethod(view: View) =
        Navigation.findNavController(view).navigate(R.id.navigation_profile)

    fun saveAndExit(view: View, passenger: Passenger?) {
        debugger("Passenger to be saved: $passenger")
        viewModelScope.launch {
            if (passenger != null) repository.saveUser(passenger)
        }
        view.toast("Profile information updated successfully")
        Navigation.findNavController(view).popBackStack()
    }

    fun logout(view: View) = repository.logoutUser(view.context)
}
