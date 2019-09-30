package com.example.geoq.mvps.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.geoq.mvps.views.AddStepToQuestView

@InjectViewState
class AddStepToQuestPresenter : MvpPresenter<AddStepToQuestView>()
{
    fun onSelectPhotoButtonClicked()
    {
        viewState.startPhotoSelector()
    }
    fun onGetLocationButtonClicked()
    {
        viewState.performGettingLocation()
    }
    fun onNextStepButtonClicked()
    {
        viewState.performNextStep()
    }
    fun onFinishButtonClicked()
    {
        viewState.performFinish()
    }
}