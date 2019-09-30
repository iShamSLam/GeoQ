package com.example.geoq.mvps.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.geoq.mvps.views.LoginView

@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>() {
    fun onLoginButtomClicked() {
        viewState.performLogin()
    }

    fun onRegisterButtonClicked() {
        viewState.redirectToRegister()
    }
}