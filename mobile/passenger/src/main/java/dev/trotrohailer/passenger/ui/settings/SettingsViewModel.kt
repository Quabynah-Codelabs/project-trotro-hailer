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
    val hasPaymentMethods = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            try {
                val response = repository.getUser(auth.uid!!, true)
                if (response.succeeded) {
                    _passenger.postValue((response as Response.Success).data)
                    debugger("From view model: ${response.data}")
                }
            } catch (e: Exception) {
                debugger(e.localizedMessage)
            }
        }
    }

    val passenger: LiveData<Passenger> = _passenger

    fun addPaymentMethod(view: View) {
        Navigation.findNavController(view).navigate(R.id.navigation_profile)
    }

    fun saveAndExit(view: View, passenger: Passenger?) {
        // todo: save and exit
        debugger("Passenger to be saved: $passenger")
    }
}
