package dev.trotrohailer.passenger.util.app

import dev.trotrohailer.shared.TrotroHailerApp

class PassengerApp : TrotroHailerApp() {

    override fun onCreate() {
        super.onCreate()
        koinModule.modules(passengerModule)
    }
}