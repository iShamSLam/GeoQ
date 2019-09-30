package com.example.geoq.mvps.views

import com.arellomobile.mvp.MvpView

interface LoginView : MvpView {
    fun performLogin()
    fun redirectToRegister()
}