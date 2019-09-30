package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName


data class Locality(

    @SerializedName("LocalityName") val localityName: String,
    @SerializedName("Thoroughfare") val thoroughfare: Thoroughfare
)