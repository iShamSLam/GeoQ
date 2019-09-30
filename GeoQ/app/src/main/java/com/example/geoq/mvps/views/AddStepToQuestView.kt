package com.example.geoq.mvps.views

import com.arellomobile.mvp.MvpView

interface AddStepToQuestView : MvpView{
    fun startPhotoSelector()
    fun performGettingLocation()
    fun performFinish()
    fun performNextStep()
}