package com.example.geoq.mvps.views

import com.arellomobile.mvp.MvpView

interface CurrentQuestView : MvpView {
    fun showHelp()
    fun checkAnswer()
    fun showFields()
}