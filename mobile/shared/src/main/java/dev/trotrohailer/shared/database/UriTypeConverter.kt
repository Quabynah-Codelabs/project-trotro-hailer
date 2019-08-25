package dev.trotrohailer.shared.database

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter
import dev.trotrohailer.shared.data.Coordinate

/**
 * Converter for [Uri] to [String] with user avatars
 */

object UriTypeConverter {

    @TypeConverter
    @JvmStatic
    fun uriToString(uri: Uri?): String? = uri.toString()

    @TypeConverter
    @JvmStatic
    fun stringToUri(string: String?): Uri? = string?.toUri()


    @TypeConverter
    @JvmStatic
    fun coodinateToString(coordinate: Coordinate): String =
        "${coordinate.latitude},${coordinate.longitude}"

    @TypeConverter
    @JvmStatic
    fun stringToCoordinate(string: String): Coordinate = if (string.isEmpty()) Coordinate.EMPTY else
        Coordinate(
            string.substring(0, string.indexOf(',').minus(1)).toDouble(),
            string.substring(string.indexOf(',').plus(1), string.length).toDouble()
        )

}