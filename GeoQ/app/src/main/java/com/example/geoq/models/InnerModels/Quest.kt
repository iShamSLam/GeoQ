package com.example.geoq.models.InnerModels

import java.io.Serializable
import javax.annotation.Nullable


data class Quest(
    var id: String = "",
    var authorUid: String = "",
    var authorName: String = "",
    var name: String = "",
    var description: String = "",
    var startPoint: Location = Location(),
    var startPointAddress: String = "",
    var steps: ArrayList<Step>? = null
) :Serializable
