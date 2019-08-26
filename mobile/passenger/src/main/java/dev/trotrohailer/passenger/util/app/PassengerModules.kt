package dev.trotrohailer.passenger.util.app

import dev.trotrohailer.passenger.ui.settings.SettingsViewModel
import dev.trotrohailer.passenger.ui.settings.SettingsViewModelFactory
import dev.trotrohailer.shared.util.Constants
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val passengerModule = module {
    factory { SettingsViewModelFactory(get(named(Constants.PASSENGERS)), get()) }
    viewModel { SettingsViewModel(get(named(Constants.PASSENGERS)), get()) }
}