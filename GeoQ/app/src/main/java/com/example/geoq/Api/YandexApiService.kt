package com.example.geoq.Api

import com.example.geoq.models.OuterModels.GeoObjectCollection
import com.example.geoq.models.OuterModels.Response
import com.example.geoq.models.OuterModels.Yandex
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexApiService {
    @GET("/1.x/?apikey=020ade6f-8746-4736-997a-7584c3f3b6ea&format=json&results=1")
    fun getGeoCodeInformation(@Query("geocode") geocode: String): Single<Yandex>
}