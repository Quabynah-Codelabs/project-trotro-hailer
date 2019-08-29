package dev.trotrohailer.driver

import dev.trotrohailer.shared.TrotroHailerApp

class DriverApp : TrotroHailerApp() {

    override fun onCreate() {
        super.onCreate()
        koinModule.modules(driverModule)
    }
}