package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName

data class Components (

	@SerializedName("kind") val kind : String,
	@SerializedName("name") val name : String
)