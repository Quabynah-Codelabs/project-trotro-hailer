package dev.trotrohailer.driver

import dev.trotrohailer.driver.main.HomeViewModel
import dev.trotrohailer.driver.main.HomeViewModelFactory
import dev.trotrohailer.driver.trips.TripsViewModel
import dev.trotrohailer.driver.viewmodel.DriverViewModel
import dev.trotrohailer.driver.viewmodel.DriverViewModelFactory
import dev.trotrohailer.shared.util.Constants
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val driverModule = module {
    factory { DriverViewModelFactory(get(named(Constants.DRIVERS)), get()) }
    viewModel { DriverViewModel(get(named(Constants.DRIVERS)), get()) }

    factory { HomeViewModelFactory(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { TripsViewModel() }
}