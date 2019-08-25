package dev.trotrohailer.shared.data

/**
 * Trip Status
 */
enum class Status(val statusInfo: String) {
    TRIP_NOT_STARTED("trip_not_started"),
    TRIP_ASSIGNED("trip_assigned"),
    STARTED_TO_PICK_UP_PASSENGER("started_to_pick_up_passenger"),
    PASSENGER_PICKUP_COMPLETED("passenger_pickup_completed"),
    STARTED_TO_DROP_OFF_PASSENGER("started_to_drop_off_passenger"),
    TRIP_COMPLETED("trip_completed")
}

fun Status.getStatusInfo(): String = statusInfo