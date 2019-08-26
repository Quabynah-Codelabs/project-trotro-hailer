package dev.trotrohailer.passenger.ui.trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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
}
