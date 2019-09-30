package com.example.geoq.models.InnerModels

import java.io.Serializable

data class Location(
    var longtitude: Double = 0.0,
    var latitude: Double = 0.0
) : Serializable