package com.dudo.sleepsafe.di.component

import com.dudo.sleepsafe.di.module.ViewModelModule
import com.dudo.sleepsafe.di.scope.ActivityScope
import com.dudo.sleepsafe.ui.tracking.TrackingActivity
import dagger.Component

@ActivityScope
@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class])
interface TrackingComponent {

    fun inject(trackingActivity: TrackingActivity)

}