package com.example.geoq.di.App

import android.app.Activity
import android.app.Application
import com.example.geoq.di.Component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class AppController : Application(), HasAndroidInjector{

    var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>? = null
        @Inject set

    override fun androidInjector(): AndroidInjector<Any>? {
        return dispatchingAndroidInjector as AndroidInjector<Any>
    }


    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }
}
