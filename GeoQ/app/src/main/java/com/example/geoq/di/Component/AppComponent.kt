package com.example.geoq.di.Component

import android.app.Application
import com.example.geoq.di.App.AppController
import com.example.geoq.di.Module.ActivityModule
import com.example.geoq.di.Module.NetModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(
    modules = [NetModule::class,
        AndroidSupportInjectionModule::class,
        ActivityModule::class
    ]
)
@Singleton
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(appController: AppController)
}