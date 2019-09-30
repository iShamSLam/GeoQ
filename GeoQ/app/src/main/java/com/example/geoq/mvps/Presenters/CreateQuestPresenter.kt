package com.example.geoq.mvps.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.geoq.mvps.views.CreateQuestView

@InjectViewState
class CreateQuestPresenter : MvpPresenter<CreateQuestView>() {
    fun onGetLocationButtonClicked() {
        viewState.performGettingLocation()
    }
    fun onNextButtonClicked()
    {
        viewState.performAddSteps()
    }
}