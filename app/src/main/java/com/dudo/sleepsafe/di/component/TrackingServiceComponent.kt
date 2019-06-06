package com.dudo.sleepsafe.di.component

import com.dudo.sleepsafe.di.scope.ServiceScope
import com.dudo.sleepsafe.service.TrackingService
import dagger.Component
import javax.inject.Singleton

@ServiceScope
@Component(dependencies = [AppComponent::class])
interface TrackingServiceComponent {

    fun inject(trackingService: TrackingService)

}