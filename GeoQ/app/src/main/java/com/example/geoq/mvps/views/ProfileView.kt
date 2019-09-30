package com.example.geoq.mvps.views

import com.arellomobile.mvp.MvpView
import com.example.geoq.OtherClasses.QuestItemProfile

interface ProfileView : MvpView {
    fun performCreatingQuest()
    fun performSignOut()
    fun showUserAttribute()
    fun showQuestInformation()
}