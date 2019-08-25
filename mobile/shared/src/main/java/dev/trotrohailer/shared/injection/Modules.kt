package dev.trotrohailer.shared.injection

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dev.trotrohailer.shared.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

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