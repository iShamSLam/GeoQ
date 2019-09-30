package com.example.geoq.mvps.views

import com.arellomobile.mvp.MvpView
import com.example.geoq.OtherClasses.StepItem

interface QuestInformationView : MvpView {
    fun showInformation()
    fun deleteQuest()
    fun subcribeToQuest()
}