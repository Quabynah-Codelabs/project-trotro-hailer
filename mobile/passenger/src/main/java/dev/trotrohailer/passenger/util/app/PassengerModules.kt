package dev.trotrohailer.passenger.util.app

import dev.trotrohailer.passenger.ui.driver.DriverViewModel
import dev.trotrohailer.passenger.ui.driver.DriverViewModelFactory
import dev.trotrohailer.passenger.ui.settings.SettingsViewModel
import dev.trotrohailer.passenger.ui.settings.SettingsViewModelFactory
import dev.trotrohailer.passenger.ui.trip.TripViewModel
import dev.trotrohailer.passenger.util.prefs.PaymentPrefs
import dev.trotrohailer.shared.util.Constants
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val passengerModule = module {
    factory { SettingsViewModelFactory(get(named(Constants.PASSENGERS)), get()) }
    factory { DriverViewModelFactory(get(named(Constants.DRIVERS))) }
    viewModel { SettingsViewModel(get(named(Constants.PASSENGERS)), get()) }
    viewModel { DriverViewModel(get(named(Constants.DRIVERS))) }
    viewModel { TripViewModel() }

    single { PaymentPrefs.get(androidContext()) }
}