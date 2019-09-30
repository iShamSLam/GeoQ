package com.example.geoq.models.InnerModels

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class GqUser(
    val uid: String = "",
    val passedQuestCount: Int = 0,
    val passedDistance: Float = 0.0F,
    val username: String = "",
    val profileImageUrl: String = "",
    val currentQuest: Quest? = null,
    val currentStep: Step? = null,
    val helpsEnable: Int = 5,
    val currentStepRiddleEnable: Boolean = false,
    val currentStepNum: Int = 0
)



