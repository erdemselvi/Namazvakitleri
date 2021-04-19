package com.erdemselvi.namazvakitleri.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NamazInterface {
    @GET("vakitler/{url}")
    fun getData(@Path("url") url: String?=""):Call<List<NamazJsonItem>>
}