package com.dudo.sleepsafe.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dudo.sleepsafe.di.key.ViewModelKey
import com.dudo.sleepsafe.ui.main.history.HistoryViewModel
import com.dudo.sleepsafe.ui.main.home.HomeViewModel
import com.dudo.sleepsafe.ui.main.settings.SettingsViewModel
import com.dudo.sleepsafe.ui.splash.SplashViewModel
import com.dudo.sleepsafe.ui.welcome.WelcomeViewModel
import com.dudo.sleepsafe.utils.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(
        factory: ViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    internal abstract fun welcomeViewModel(viewModel: WelcomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    internal abstract fun splashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun homeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun settingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    internal abstract fun historyViewModel(viewModel: HistoryViewModel): ViewModel

}