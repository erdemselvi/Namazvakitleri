package com.erdemselvi.namazvakitleri.konumapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SehirlerJsonItem(
    @SerializedName("SehirAdi")
    @Expose
    val SehirAdi: String,
    @SerializedName("SehirAdiEn")
    @Expose
    val SehirAdiEn: String,
    @SerializedName("SehirID")
    @Expose
    val SehirID: String
): Serializable {

}