package dev.trotrohailer.passenger.ui.trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class TripViewModel : ViewModel() {
    val isLoading: LiveData<Boolean>
    val swipeRefreshing: LiveData<Boolean>

    init {

    }

    fun onSwipeRefresh() {
        // todo
    }
}
