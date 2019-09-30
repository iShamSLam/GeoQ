package com.example.geoq.mvps.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.geoq.mvps.views.QuestListView

@InjectViewState
class QuestListPresenter : MvpPresenter<QuestListView>() {
    fun onLoadButtonClicked() {
        viewState.perfromLoading()
    }

    fun onAdapterClicked()
    {
        viewState.showExtra()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setUp()
    }
}