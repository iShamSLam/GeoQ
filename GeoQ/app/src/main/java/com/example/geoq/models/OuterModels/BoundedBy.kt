package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName


data class BoundedBy (

	@SerializedName("Envelope") val envelope : Envelope
)