package com.dudo.sleepsafe.di.component

import com.dudo.sleepsafe.di.module.ViewModelModule
import com.dudo.sleepsafe.di.scope.ActivityScope
import com.dudo.sleepsafe.ui.splash.SplashActivity
import dagger.Component

@ActivityScope
@Component(modules = [ViewModelModule::class], dependencies = [AppComponent::class])
interface SplashComponent {

    fun inject(splashActivity: SplashActivity)

}