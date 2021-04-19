package com.erdemselvi.namazvakitleri.database

data class Konumlar(var id:Int,
                    var ulkeId:String,
                    var ulke:String,
                    var sehirId:String,
                    var sehir:String,
                    var ilceId:String,
                    var ilce:String) {
}