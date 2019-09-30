package com.example.geoq.mvps.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.geoq.mvps.views.CurrentQuestView

@InjectViewState
class CurrentQuestPresenter : MvpPresenter<CurrentQuestView>() {
    fun onHelpButtonClicked() {
        viewState.showHelp()
    }

    fun onCheckButtonClicked() {
        viewState.checkAnswer()
    }

    fun onStepPassed() {
        viewState.showFields()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showFields()
    }
}