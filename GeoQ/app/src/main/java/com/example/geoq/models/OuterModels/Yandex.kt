package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName

data class Yandex(

    @SerializedName("response") val response: Response
)