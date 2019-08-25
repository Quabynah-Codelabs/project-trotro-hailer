package dev.trotrohailer.shared.data

import android.net.Uri
import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.trotrohailer.shared.glide.load
import dev.trotrohailer.shared.util.Constants
import kotlinx.android.parcel.Parcelize
import java.util.*

interface User : Parcelable {
    val id: String
}

@Entity(tableName = Constants.PASSENGERS)
@Parcelize
data class Passenger(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var name: String?,
    var avatar: Uri? = Uri.EMPTY,
    var phone: String? = null,
    var tripId: String? = null,
    var isOnTrip: Boolean = !tripId.isNullOrEmpty(),
    var rating: Float = 1.0f,
    var coordinate: Coordinate = Coordinate.EMPTY
) : User {
    constructor() : this("", "")
}

@Entity(tableName = Constants.DRIVERS)
@Parcelize
data class Driver(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var name: String?,
    var vehicle: String?,
    var vehicleNumber: String?,
    var avatar: Uri? = Uri.EMPTY
) : User {
    constructor() : this("", "", "", "")
}


object BindingAdapters {
    @JvmStatic
    @BindingAdapter("app:avatar")
    fun loadCircleAvatar(imageView: ImageView, url: Uri?) = imageView.load(url, circleCrop = true)
}
