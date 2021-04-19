package com.erdemselvi.namazvakitleri.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseKonum(context: Context):SQLiteOpenHelper(context,"konum",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE konumlar (id INTEGER PRIMARY KEY AUTOINCREMENT,ulkeId TEXT,ulke TEXT, sehirId TEXT, sehir TEXT," +
                "ilceId TEXT, ilce TEXT);")

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

        db?.execSQL("DROP TABLE IF EXISTS konum")
        onCreate(db) //hata olursa tabloyu sil ve oluştur fonksiyonuna git

    }


}