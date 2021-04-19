package com.erdemselvi.namazvakitleri.konumapi

import com.erdemselvi.namazvakitleri.api.NamazJsonItem
import retrofit2.Call
import retrofit2.http.GET

interface KonumInterface {
    @GET("/ulkeler")
    fun getDataKonum(): Call<List<KonumJsonItem>>
}