package com.erdemselvi.namazvakitleri.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NamazJsonItem(
    @SerializedName("Aksam")
    val Aksam: String,
    @SerializedName("AyinSekliURL")
    val AyinSekliURL: String,
    @SerializedName("Gunes")
    val Gunes: String,
    @SerializedName("GunesBatis")
    val GunesBatis: String,
    @SerializedName("GunesDogus")
    val GunesDogus: String,
    @SerializedName("HicriTarihKisa")
    val HicriTarihKisa: String,
    @SerializedName("HicriTarihKisaIso8601")
    val HicriTarihKisaIso8601: Any,
    @SerializedName("HicriTarihUzun")
    val HicriTarihUzun: String,
    @SerializedName("HicriTarihUzunIso8601")
    val HicriTarihUzunIso8601: Any,
    @SerializedName("Ikindi")
    val Ikindi: String,
    @SerializedName("Imsak")
    val Imsak: String,
    @SerializedName("KibleSaati")
    val KibleSaati: String,
    @SerializedName("MiladiTarihKisa")
    val MiladiTarihKisa: String,
    @SerializedName("MiladiTarihKisaIso8601")
    val MiladiTarihKisaIso8601: String,
    @SerializedName("MiladiTarihUzun")
    val MiladiTarihUzun: String,
    @SerializedName("MiladiTarihUzunIso8601")
    val MiladiTarihUzunIso8601: String,
    @SerializedName("Ogle")
    val Ogle: String,
    @SerializedName("Yatsi")
    val Yatsi: String
) : Serializable{

}