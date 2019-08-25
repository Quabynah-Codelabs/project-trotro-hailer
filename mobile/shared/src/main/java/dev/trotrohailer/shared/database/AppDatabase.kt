package dev.trotrohailer.shared.database

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.room.*
import dev.trotrohailer.shared.data.Driver
import dev.trotrohailer.shared.data.Passenger

@TypeConverters(UriTypeConverter::class)
@Database(entities = [Passenger::class, Driver::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun passengerDao(): PassengerDao
    abstract fun driverDao(): DriverDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context, AppDatabase::class.java, "trotro_hailer.db")
                .fallbackToDestructiveMigrationOnDowngrade()
                .build().also { instance = it }
        }
    }

}

/**
 * Converter for [Uri] to [String] with user avatars
 */
object UriTypeConverter {

    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri.toString()

    @TypeConverter
    fun stringToUri(string: String?): Uri? = string?.toUri()


}