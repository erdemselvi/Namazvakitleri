package com.erdemselvi.namazvakitleri.database

import android.content.ContentValues
import android.database.Cursor

class VakitlerDao {
    fun vakitEkle(vt:DatabaseVakitler, imsak:String,  gunes:String,  ogle:String, ikindi:String,
                   aksam:String,yatsi:String, miladiTarih:String, hicriTarih:String, ayUrl:String){

        val db=vt.writableDatabase
        val values= ContentValues()

        values.put("imsak",imsak)
        values.put("gunes",gunes)
        values.put("ogle",ogle)
        values.put("ikindi",ikindi)
        values.put("aksam",aksam)
        values.put("yatsi",yatsi)
        values.put("miladiTarih",miladiTarih)
        values.put("hicriTarih",hicriTarih)
        values.put("ayUrl",ayUrl)

        db.insertOrThrow("vakitler", null,values)
        db.close()


    }
    fun elemanSayısı(vt:DatabaseVakitler):Int {
        val countQuery = "SELECT  * FROM vakitler"
        val db = vt.readableDatabase
        val cursor = db.rawQuery(countQuery, null);
        val rowCount = cursor.getCount()
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }
    fun vakitListele(vt:DatabaseVakitler): ArrayList<Vakitler> {

        val vakitlerArrayList=ArrayList<Vakitler>()

        val db=vt.writableDatabase

        val cursor= db.rawQuery("SELECT * FROM vakitler", null)
        while (cursor.moveToNext()) {

            val vakit = Vakitler(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("imsak")),cursor.getString(cursor.getColumnIndex("gunes")),
                    cursor.getString(cursor.getColumnIndex("ogle")),cursor.getString(cursor.getColumnIndex("ikindi")),
                    cursor.getString(cursor.getColumnIndex("aksam")),cursor.getString(cursor.getColumnIndex("yatsi")),
                    cursor.getString(cursor.getColumnIndex("miladiTarih")),cursor.getString(cursor.getColumnIndex("hicriTarih")),
                    cursor.getString(cursor.getColumnIndex("ayUrl")))
            vakitlerArrayList.add(vakit)
        }
        db.close()
            return vakitlerArrayList

    }
    fun vakitSil(vt: DatabaseVakitler,id:Int){

        val db=vt.writableDatabase

        db.delete("vakitler","id=?", arrayOf(id.toString()))
        db.close()

    }
}