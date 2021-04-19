package com.erdemselvi.namazvakitleri.konumapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SehirlerInterface {
    @GET("/sehirler/{url}")
    fun getDataSehirler(@Path ("url") url: String?="2"): Call<List<SehirlerJsonItem>>
}