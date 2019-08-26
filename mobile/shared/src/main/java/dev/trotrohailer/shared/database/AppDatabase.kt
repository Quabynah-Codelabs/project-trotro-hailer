package dev.trotrohailer.shared.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.trotrohailer.shared.data.Driver
import dev.trotrohailer.shared.data.Passenger

@TypeConverters(UriTypeConverter::class)
@Database(entities = [Passenger::class, Driver::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun passengerDao(): PassengerDao
    abstract fun driverDao(): DriverDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context, AppDatabase::class.java, "trotro_hailer.db")
                .fallbackToDestructiveMigration()
                .build().also { instance = it }
        }
    }

}