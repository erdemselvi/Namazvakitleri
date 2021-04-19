package com.erdemselvi.namazvakitleri.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.erdemselvi.namazvakitleri.KonumSecActivity

class DatabaseVakitler(context: Context): SQLiteOpenHelper(context,"vakit",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE vakitler (id INTEGER PRIMARY KEY AUTOINCREMENT,imsak TEXT,gunes TEXT, ogle TEXT, ikindi TEXT," +
                "aksam TEXT, yatsi TEXT,miladiTarih TEXT,hicriTarih TEXT,ayUrl TEXT);")

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

        db?.execSQL("DROP TABLE IF EXISTS vakitler")
        onCreate(db) //hata olursa tabloyu sil ve oluştur fonksiyonuna git

    }


}