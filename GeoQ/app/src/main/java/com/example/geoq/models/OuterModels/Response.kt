package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName


data class Response(

    @SerializedName("GeoObjectCollection") val geoObjectCollection: GeoObjectCollection
)