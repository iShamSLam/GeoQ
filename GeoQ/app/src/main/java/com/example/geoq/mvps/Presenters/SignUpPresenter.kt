package com.example.geoq.mvps.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.geoq.mvps.views.SignUpView

@InjectViewState
class SignUpPresenter : MvpPresenter<SignUpView>() {
    fun onSingUpButtomClicked() {
        viewState.performRegister()
    }

    fun onSelectPhotoBtmClicked() {
        viewState.startPhotoSelector()
    }
}