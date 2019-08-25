package dev.trotrohailer.shared.util

import com.google.firebase.firestore.FirebaseFirestore
import dev.trotrohailer.shared.util.Constants.DRIVERS
import dev.trotrohailer.shared.util.Constants.PASSENGERS

fun debugger(msg: Any?) = println("TroTro ==> ${msg.toString()}")

fun FirebaseFirestore.passengerDocument(userId: String) = collection(PASSENGERS).document(userId)
fun FirebaseFirestore.passengers() = collection(PASSENGERS)

fun FirebaseFirestore.driverDocument(userId: String) = collection(DRIVERS).document(userId)
fun FirebaseFirestore.drivers() = collection(DRIVERS)


object Constants {
    const val PASSENGERS = "passengers"
    const val DRIVERS = "drivers"
    const val TRIPS = "trips"
}