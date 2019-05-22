package com.dudo.sleepsafe.di.component

import com.dudo.sleepsafe.di.module.ViewModelModule
import com.dudo.sleepsafe.di.scope.FragmentScope
import com.dudo.sleepsafe.ui.main.settings.SettingsFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class])
interface SettingsComponent {

    fun inject(settingsFragment: SettingsFragment)

}