package dev.trotrohailer.shared.injection

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dev.trotrohailer.shared.data.Driver
import dev.trotrohailer.shared.data.Passenger
import dev.trotrohailer.shared.database.AppDatabase
import dev.trotrohailer.shared.datasource.DriverRepository
import dev.trotrohailer.shared.datasource.PassengerRepository
import dev.trotrohailer.shared.datasource.UserRepository
import dev.trotrohailer.shared.util.Constants
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun loadModules() = mutableListOf(firebaseModule, databaseModule, appModule)

/**
 * Remote database DSL
 */
val firebaseModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseMessaging.getInstance() }
    single { FirebaseDatabase.getInstance() }
}

/**
 * Local database DSL
 */
val databaseModule = module {
    single { AppDatabase.get(androidContext()) }
    single { get<AppDatabase>().driverDao() }
    single { get<AppDatabase>().passengerDao() }
}

val appModule = module {
    single<UserRepository<Passenger>>(named(Constants.PASSENGERS)) {
        PassengerRepository(
            get(),
            get()
        )
    }

    single<UserRepository<Driver>>(named(Constants.DRIVERS)) { DriverRepository(get(), get()) }
}