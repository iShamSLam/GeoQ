package com.example.geoq.mvps.views

import com.arellomobile.mvp.MvpView

interface QuestListView : MvpView {
    fun perfromLoading()
    fun setUp()
    fun showExtra()
}