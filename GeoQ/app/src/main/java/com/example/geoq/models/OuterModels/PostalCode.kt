package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName


data class PostalCode(

    @SerializedName("PostalCodeNumber") val postalCodeNumber: Int
)