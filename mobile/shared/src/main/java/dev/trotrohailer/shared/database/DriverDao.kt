package dev.trotrohailer.shared.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import dev.trotrohailer.shared.data.Driver

@Dao
interface DriverDao : BaseDao<Driver> {

    @Query("SELECT * FROM drivers WHERE id = :id")
    fun getDriver(id: String): LiveData<Driver>

    @Query("SELECT * FROM drivers WHERE id = :id")
    fun getDriverAsync(id: String): Driver

    @Query("SELECT * FROM drivers")
    fun getAllDrivers(): LiveData<MutableList<Driver>>

    @Query("SELECT * FROM drivers")
    fun getAllDriversAsync(): MutableList<Driver>

}