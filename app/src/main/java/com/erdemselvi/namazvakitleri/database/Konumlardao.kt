package com.erdemselvi.namazvakitleri.database

import android.content.ContentValues

class Konumlardao {
    fun konumEkle(vt:DatabaseKonum,ulkeId:String,ulke:String,
                 sehirId:String,sehir:String, ilceId:String, ilce:String){

        val db=vt.writableDatabase
        val values=ContentValues()

        values.put("ulkeId",ulkeId)
        values.put("ulke",ulke)
        values.put("sehirId",sehirId)
        values.put("sehir",sehir)
        values.put("ilceId",ilceId)
        values.put("ilce",ilce)

        db.insertOrThrow("konumlar", null,values)
        db.close()


    }

    fun konumListele(vt:DatabaseKonum):ArrayList<Konumlar>{

        val konumlarArrayList=ArrayList<Konumlar>()

        val db=vt.writableDatabase

       val cursor= db.rawQuery("SELECT * FROM konumlar", null)
        while (cursor.moveToNext()) {

            val konum = Konumlar(cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("ulkeId")),cursor.getString(cursor.getColumnIndex("ulke")),
                cursor.getString(cursor.getColumnIndex("sehirId")),cursor.getString(cursor.getColumnIndex("sehir")),
                cursor.getString(cursor.getColumnIndex("ilceId")),cursor.getString(cursor.getColumnIndex("ilce")))
            konumlarArrayList.add(konum)
        }
        db.close()
        return konumlarArrayList

    }
    fun konumSil(vt: DatabaseKonum,id:Int){

        val db=vt.writableDatabase

        db.delete("konumlar","id=?", arrayOf(id.toString()))
        db.close()

    }
}