package com.erdemselvi.namazvakitleri.konumapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KonumJsonItem(
    @SerializedName("UlkeAdi")
    @Expose
    val UlkeAdi: String,
    @SerializedName("UlkeAdiEn")
    @Expose
    val UlkeAdiEn: String,
    @SerializedName("UlkeID")
    @Expose
    val UlkeID: String
): Serializable