package com.erdemselvi.namazvakitleri.konumapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IlcelerInterface {
    @GET("/ilceler/{url}")
    fun getDataIlceler(@Path("url") url: String?="5000"): Call<IlcelerJson>
}