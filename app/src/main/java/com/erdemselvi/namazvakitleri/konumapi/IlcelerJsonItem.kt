package com.erdemselvi.namazvakitleri.konumapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class IlcelerJsonItem(
    @SerializedName("IlceAdi")
    @Expose
    val IlceAdi: String,
    @SerializedName("IlceAdiEn")
    @Expose
    val IlceAdiEn: String,
    @SerializedName("IlceID")
    @Expose
    val IlceID: String
): Serializable