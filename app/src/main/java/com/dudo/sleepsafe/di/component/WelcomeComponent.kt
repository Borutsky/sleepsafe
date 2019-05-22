package com.dudo.sleepsafe.di.component

import com.dudo.sleepsafe.di.module.PreferencesModule
import com.dudo.sleepsafe.di.module.ViewModelModule
import com.dudo.sleepsafe.di.scope.ActivityScope
import com.dudo.sleepsafe.ui.welcome.WelcomeActivity
import dagger.Component
import javax.inject.Singleton

@ActivityScope
@Component(modules = [ViewModelModule::class], dependencies = [AppComponent::class])
interface WelcomeComponent {

    fun inject(welcomeActivity: WelcomeActivity)

}