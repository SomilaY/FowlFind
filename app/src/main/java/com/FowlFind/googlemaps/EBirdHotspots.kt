package com.FowlFind.googlemaps

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface EbirdService {
    @Headers("x-ebirdapitoken: 4dd587sb50sj")
    @GET("ref/hotspot/geo")
    fun getHotspots(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("dist") dist: Float = 10f,
        @Query("fmt") fmt: String = "json"
    ): Call<List<Hotspot>>
}

data class Hotspot(
    val lat: Double,
    val lng: Double,
    // Add other fields as needed
)


