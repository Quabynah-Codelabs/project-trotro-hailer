package dev.trotrohailer.shared.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Address(val displayAddress: String, val coordinate: Coordinate) : Parcelable {
    constructor() : this("", Coordinate.EMPTY)
}

@Parcelize
data class Coordinate(val latitude: Double, val longitude: Double) : Parcelable {
    constructor() : this(5.22, -0.124)

    companion object {
        val EMPTY = Coordinate()
    }
}

@Parcelize
data class HyperTrackDetails(
    @PropertyName("collection_id")
    val collectionId: String,
    @PropertyName("drop_action_id")
    val dropActionId: String,
    @PropertyName("pickup_action_id")
    val pickupActionId: String,
    @PropertyName("drop_unique_id")
    val dropUniqueId: String,
    @PropertyName("pickup_unique_id")
    val pickupUniqueId: String,
    @PropertyName("drop_tracking_url")
    val pickupTrackingUrl: String
) : Parcelable {
    constructor() : this("", "", "", "", "", "")
}

@Parcelize
data class Trip(
    val id: String = UUID.randomUUID().toString(),
    var drop: Address,
    var pickup: Address,
    var status: Status,
    var passenger: String,
    var driver: String,
    var hypertrack: HyperTrackDetails
) : Parcelable {
    constructor() : this(
        "",
        Address(),
        Address(),
        Status.TRIP_NOT_STARTED,
        "",
        "",
        HyperTrackDetails()
    )
}