package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName


data class Thoroughfare(

    @SerializedName("ThoroughfareName") val thoroughfareName: String,
    @SerializedName("Premise") val premise: Premise
)