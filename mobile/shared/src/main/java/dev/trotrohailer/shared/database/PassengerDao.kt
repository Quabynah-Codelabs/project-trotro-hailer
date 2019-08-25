package dev.trotrohailer.shared.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import dev.trotrohailer.shared.data.Passenger

@Dao
interface PassengerDao : BaseDao<Passenger> {

    @Query("SELECT * FROM passengers WHERE id = :id")
    fun getPassenger(id: String): LiveData<Passenger>

    @Query("SELECT * FROM passengers WHERE id = :id")
    fun getPassengerAsync(id: String): Passenger

    @Query("SELECT * FROM passengers")
    fun getPassengers(): MutableList<Passenger>

}