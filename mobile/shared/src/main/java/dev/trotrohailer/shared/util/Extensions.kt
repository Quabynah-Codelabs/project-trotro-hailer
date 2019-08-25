package dev.trotrohailer.shared.util

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.text.trimmedLength
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dev.trotrohailer.shared.data.Driver
import dev.trotrohailer.shared.data.Passenger
import dev.trotrohailer.shared.util.Constants.DRIVERS
import dev.trotrohailer.shared.util.Constants.PASSENGERS
import java.util.*

fun debugger(msg: Any?) = println("TroTro ==> ${msg.toString()}")

fun FirebaseFirestore.passengerDocument(userId: String) = collection(PASSENGERS).document(userId)
fun FirebaseFirestore.passengers() = collection(PASSENGERS)

fun FirebaseFirestore.driverDocument(userId: String) = collection(DRIVERS).document(userId)
fun FirebaseFirestore.drivers() = collection(DRIVERS)


fun FragmentActivity.intentTo(
    target: Class<out FragmentActivity>,
    finished: Boolean = false,
    bundle: Bundle = bundleOf()
) {
    startActivity(Intent(applicationContext, target).apply { putExtras(bundle) })
    if (finished) finishAffinity()
}


fun FirebaseUser.mapToPassenger(): Passenger = Passenger(
    UUID.randomUUID().toString(),
    displayName ?: "No username",
    if (photoUrl == null) null else if (photoUrl.toString().trimmedLength() > 80) null else photoUrl,
    phoneNumber
)

fun FirebaseUser.mapToDriver(): Driver =
    Driver(
        UUID.randomUUID().toString(), displayName ?: "No username", "", "",
        if (photoUrl == null) null else if (photoUrl.toString().trimmedLength() > 80) null else photoUrl
    )

object Constants {
    const val PASSENGERS = "passengers"
    const val DRIVERS = "drivers"
    const val TRIPS = "trips"
}