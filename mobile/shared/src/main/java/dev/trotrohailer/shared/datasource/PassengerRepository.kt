package dev.trotrohailer.shared.datasource

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dev.trotrohailer.shared.data.Passenger
import dev.trotrohailer.shared.database.PassengerDao
import dev.trotrohailer.shared.result.Response
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.passengerDocument
import dev.trotrohailer.shared.util.passengers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PassengerRepository constructor(
    private val passengerDao: PassengerDao,
    private val db: FirebaseFirestore
) : UserRepository<Passenger> {

    override suspend fun getUser(id: String, refresh: Boolean): Response<Passenger> =
        withContext(Dispatchers.IO) {
            if (refresh) {
                try {
                    val passenger =
                        Tasks.await(db.passengerDocument(id).get()).toObject(Passenger::class.java)
                    if (passenger != null) passengerDao.insert(passenger)
                    return@withContext Response.Success(passenger)
                } catch (e: Exception) {
                    return@withContext Response.Error(e)
                }
            } else {
                return@withContext Response.Success(passengerDao.getPassengerAsync(id))
            }
        }

    override suspend fun getUsers(refresh: Boolean): Response<MutableList<Passenger>> =
        withContext(Dispatchers.IO) {
            if (refresh) {
                try {
                    return@withContext Response.Success(
                        Tasks.await(db.passengers().get()).toObjects(
                            Passenger::class.java
                        ).apply {
                            if (this.isNotEmpty()) {
                                passengerDao.insertAll(this)
                            }
                        }
                    )
                } catch (e: Exception) {
                    return@withContext Response.Error(e)
                }
            } else {
                return@withContext Response.Success(passengerDao.getPassengers())
            }

        }

    override suspend fun saveUser(user: Passenger) = withContext(Dispatchers.IO) {
        passengerDao.insert(user)
        try {
            Tasks.await(db.passengerDocument(user.id).set(user, SetOptions.merge()))
            null
        } catch (e: Exception) {
            debugger(e.localizedMessage)
            null
        }
    }

    override suspend fun updateUser(user: Passenger) = withContext(Dispatchers.IO) {
        try {
            Tasks.await(db.passengerDocument(user.id).set(user, SetOptions.merge()))
            passengerDao.update(user)
            null
        } catch (e: Exception) {
            debugger(e.localizedMessage)
            null
        }
    }

    override suspend fun deleteUser(user: Passenger) = withContext(Dispatchers.IO) {
        try {
            Tasks.await(db.passengerDocument(user.id).delete())
            passengerDao.delete(user)
            null
        } catch (e: Exception) {
            debugger(e.localizedMessage)
            null
        }
    }

    fun logoutUser(context: Context) {
        AuthUI.getInstance().signOut(context).addOnCompleteListener { }.addOnFailureListener { }
    }
}