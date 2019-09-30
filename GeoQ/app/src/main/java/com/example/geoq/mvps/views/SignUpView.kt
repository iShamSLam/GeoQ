package com.example.geoq.mvps.views

import com.arellomobile.mvp.MvpView

interface SignUpView : MvpView {
    fun performRegister()
    fun startPhotoSelector()
}