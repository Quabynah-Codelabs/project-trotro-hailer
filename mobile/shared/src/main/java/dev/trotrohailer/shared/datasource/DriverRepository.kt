package dev.trotrohailer.shared.datasource

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dev.trotrohailer.shared.data.Driver
import dev.trotrohailer.shared.database.DriverDao
import dev.trotrohailer.shared.result.Response
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.driverDocument
import dev.trotrohailer.shared.util.drivers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DriverRepository constructor(
    private val driverDao: DriverDao,
    private val db: FirebaseFirestore
) : UserRepository<Driver> {

    override suspend fun getUser(id: String, refresh: Boolean): Response<Driver> =
        withContext(Dispatchers.IO) {
            if (refresh) {
                try {
                    val driver =
                        Tasks.await(db.driverDocument(id).get()).toObject(Driver::class.java)
                    if (driver != null) driverDao.insert(driver)
                    return@withContext Response.Success(driver)
                } catch (e: Exception) {
                    return@withContext Response.Error(e)
                }
            } else {
                return@withContext Response.Success(driverDao.getDriverAsync(id))
            }
        }

    override suspend fun getUsers(refresh: Boolean): Response<MutableList<Driver>> =
        withContext(Dispatchers.IO) {
            if (refresh) {
                try {
                    return@withContext Response.Success(
                        Tasks.await(db.drivers().get()).toObjects(
                            Driver::class.java
                        ).apply {
                            if (this.isNotEmpty()) {
                                driverDao.insertAll(this)
                            }
                        }
                    )
                } catch (e: Exception) {
                    return@withContext Response.Error(e)
                }
            } else {
                return@withContext Response.Success(driverDao.getAllDriversAsync())
            }

        }

    override suspend fun saveUser(user: Driver) = withContext(Dispatchers.IO) {
        try {
            db.runTransaction { transaction ->
                val driverDocument = db.driverDocument(user.id)
                val snapshot = transaction.get(driverDocument)
                if (snapshot.exists()) {
                    driverDao.insert(snapshot.toObject(Driver::class.java) ?: user)
                } else {
                    transaction.set(driverDocument, user, SetOptions.merge())
                    driverDao.insert(user)
                }
                null
            }
            null
        } catch (e: Exception) {
            debugger(e.localizedMessage)
            null
        }
    }

    override suspend fun updateUser(user: Driver) = withContext(Dispatchers.IO) {
        try {
            Tasks.await(db.driverDocument(user.id).set(user, SetOptions.merge()))
            driverDao.update(user)
            null
        } catch (e: Exception) {
            debugger(e.localizedMessage)
            null
        }
    }

    override suspend fun deleteUser(user: Driver) = withContext(Dispatchers.IO) {
        try {
            Tasks.await(db.driverDocument(user.id).delete())
            driverDao.delete(user)
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