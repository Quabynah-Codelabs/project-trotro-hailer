package dev.trotrohailer.shared.datasource

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import dev.trotrohailer.shared.data.Passenger
import dev.trotrohailer.shared.database.PassengerDao
import dev.trotrohailer.shared.result.Response
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
                        )
                    )
                } catch (e: Exception) {
                    return@withContext Response.Error(e)
                }
            } else {
                return@withContext Response.Success(passengerDao.getPassengers())
            }

        }
}