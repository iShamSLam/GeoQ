package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName


data class AdministrativeArea (

	@SerializedName("AdministrativeAreaName") val administrativeAreaName : String,
	@SerializedName("Locality") val locality : Locality
)