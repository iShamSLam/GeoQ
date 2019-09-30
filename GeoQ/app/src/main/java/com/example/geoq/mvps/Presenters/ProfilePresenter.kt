package com.example.geoq.mvps.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.geoq.OtherClasses.QuestItemProfile
import com.example.geoq.mvps.views.ProfileView

@InjectViewState
class ProfilePresenter : MvpPresenter<ProfileView>() {
    fun onCreateQuestButtonClicked() {
        viewState.performCreatingQuest()
    }

    fun onSignOutButtonClicked() {
        viewState.performSignOut()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showUserAttribute()
    }
    fun onAdapterItemClicked()
    {
        viewState.showQuestInformation()
    }
}