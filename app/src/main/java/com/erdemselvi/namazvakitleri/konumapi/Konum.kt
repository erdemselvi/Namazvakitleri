package com.erdemselvi.namazvakitleri.konumapi

import java.io.Serializable

data class Konum(
    var ulkeAdi:String?="",

    var sehirAdi:String?="",

    var ilceAdi:String?="",

    var ilceKodu:String?="")
                :Serializable{
}