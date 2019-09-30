package com.example.geoq.models.InnerModels

import java.io.Serializable


data class Step(
    var riddle: String = "",
    var finishPoint: Location = Location(),
    var help: String = "",
    var helpPicture: String = ""
) : Serializable


