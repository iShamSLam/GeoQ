package com.example.geoq.models.OuterModels

import com.google.gson.annotations.SerializedName


data class AddressDetails (

	@SerializedName("Country") val country : Country
)