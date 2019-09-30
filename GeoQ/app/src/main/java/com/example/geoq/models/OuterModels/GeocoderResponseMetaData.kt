package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName


data class GeocoderResponseMetaData(

    @SerializedName("request") val request: String,
    @SerializedName("results") val results: Int,
    @SerializedName("found") val found: Int
)