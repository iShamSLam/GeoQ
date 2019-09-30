package com.example.geoq.di.Module

import com.example.geoq.ui.activities.AddStepToQuestActivity
import com.example.geoq.ui.activities.CreateQuestActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector()
    abstract fun contributeCreateQuestActivity(): CreateQuestActivity

    @ContributesAndroidInjector()
    abstract fun contributeAddStepToQuestActivity(): AddStepToQuestActivity

}