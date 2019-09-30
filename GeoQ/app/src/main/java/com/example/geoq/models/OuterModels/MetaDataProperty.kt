package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName


data class MetaDataProperty(

    @SerializedName("GeocoderMetaData") val geocoderMetaData: GeocoderMetaData
)