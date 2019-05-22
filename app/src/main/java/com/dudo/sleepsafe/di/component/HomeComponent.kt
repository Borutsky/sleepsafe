package com.dudo.sleepsafe.di.component

import com.dudo.sleepsafe.di.module.ViewModelModule
import com.dudo.sleepsafe.di.scope.FragmentScope
import com.dudo.sleepsafe.ui.main.home.HomeFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class])
interface HomeComponent {

    fun inject(homeFragment: HomeFragment)

}