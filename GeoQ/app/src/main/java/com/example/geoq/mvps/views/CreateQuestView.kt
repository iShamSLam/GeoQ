package com.example.geoq.mvps.views

import com.arellomobile.mvp.MvpView

interface CreateQuestView : MvpView {
    fun performGettingLocation()
    fun performAddSteps()

}