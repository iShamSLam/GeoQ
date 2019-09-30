package com.example.geoq.mvps.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.geoq.mvps.views.BottomNavigationView

@InjectViewState
class BottomNavigationPresenter : MvpPresenter<BottomNavigationView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.verifyUserIsLoggedIn()
    }
}