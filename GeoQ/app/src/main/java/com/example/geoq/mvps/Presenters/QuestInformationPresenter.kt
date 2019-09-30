package com.example.geoq.mvps.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.geoq.mvps.views.QuestInformationView

@InjectViewState
class QuestInformationPresenter : MvpPresenter<QuestInformationView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showInformation()
    }

    fun onDeleteButtonClicked() {
        viewState.deleteQuest()
    }

    fun onBeginQuestButtonClicked() {
        viewState.subcribeToQuest()
    }
}