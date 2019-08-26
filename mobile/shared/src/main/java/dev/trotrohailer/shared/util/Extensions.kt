package dev.trotrohailer.shared.util

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcel
import android.view.View
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.ParcelCompat
import androidx.core.os.bundleOf
import androidx.core.text.trimmedLength
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dev.trotrohailer.shared.data.Driver
import dev.trotrohailer.shared.data.Passenger
import dev.trotrohailer.shared.util.Constants.DRIVERS
import dev.trotrohailer.shared.util.Constants.PASSENGERS

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

/**
 * Linearly interpolate between two values.
 */
fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
}

/**
 * Alternative to Resources.getDimension() for values that are TYPE_FLOAT.
 */
fun Resources.getFloatUsingCompat(@DimenRes resId: Int): Float {
    return ResourcesCompat.getFloat(this, resId)
}

/** Write a boolean to a Parcel. */
fun Parcel.writeBooleanUsingCompat(value: Boolean) = ParcelCompat.writeBoolean(this, value)

/** Read a boolean from a Parcel. */
fun Parcel.readBooleanUsingCompat() = ParcelCompat.readBoolean(this)

fun FirebaseUser.mapToPassenger(): Passenger = Passenger(
    uid,
    displayName ?: "No username",
    if (photoUrl == null) null else if (photoUrl.toString().trimmedLength() > 80) null else photoUrl.toString(),
    phoneNumber
)

fun FirebaseUser.mapToDriver(): Driver =
    Driver(
        uid, displayName ?: "No username", "", "",
        if (photoUrl == null) null else if (photoUrl.toString().trimmedLength() > 80) null else photoUrl.toString()
    )

fun View.toast(msg: Any?) = Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show()

object Constants {
    const val PASSENGERS = "passengers"
    const val DRIVERS = "drivers"
    const val TRIPS = "trips"
}