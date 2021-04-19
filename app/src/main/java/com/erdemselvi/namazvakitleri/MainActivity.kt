package com.erdemselvi.namazvakitleri

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.namazvakitleri.api.NamazInterface
import com.erdemselvi.namazvakitleri.api.NamazJsonItem
import com.erdemselvi.namazvakitleri.database.DatabaseKonum
import com.erdemselvi.namazvakitleri.database.DatabaseVakitler
import com.erdemselvi.namazvakitleri.database.Konumlardao
import com.erdemselvi.namazvakitleri.database.Vakitler
import com.erdemselvi.namazvakitleri.database.VakitlerDao
import kotlinx.android.synthetic.main.activity_konum_sec.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val BASE_URL="http://ezanvakti.herokuapp.com"

class MainActivity : AppCompatActivity() {

    lateinit var imsak:String
    lateinit var gunes:String
    lateinit var ogle:String
    lateinit var ikindi:String
    lateinit var aksam:String
    lateinit var yatsi:String

//    private val CH_Id="id1"
//    private val notifyId=101

    lateinit var konumId:String
    private lateinit var ilceAdi:String
    lateinit var sehirAdi:String

    private lateinit var myAdapter:NamazVakitleriAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var namazListe:List<NamazJsonItem>


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getActionBar()?.setDisplayHomeAsUpEnabled(true);

        veritabaniSorgula()

        if (veritabaniVakitSorgula()==false){
            listele()
        }
        else{
            vakitleriAdapterdaGoster()
        }

        Log.e("konumıd",konumId)
        if (konumId.isEmpty()){
            val intent=Intent(this,KonumSecActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (sehirAdi==ilceAdi){
            tvSehir.text = sehirAdi
        }else {
            tvSehir.text = "$sehirAdi-$ilceAdi"
        }

        rvAylıkNamazVakitleri.setHasFixedSize(true)
        linearLayoutManager= LinearLayoutManager(this)
        rvAylıkNamazVakitleri.layoutManager= StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        vakitleriAdapterdaGoster()

        tvSehir.setOnClickListener {
            val vt= DatabaseKonum(this)
            val konumlarListe= Konumlardao().konumListele(vt)
            for (k in konumlarListe){
                Konumlardao().konumSil(vt,k.id)
            }

            val intent=Intent(this,KonumSecActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun tarihBul():String {

        val tarihFormat=SimpleDateFormat("dd.MM.yyyy")
        val tarih= Date()
        val simdiTarih=tarihFormat.format(tarih)
//        Log.e("tarihhh",simdiTarih)


        return simdiTarih.toString()

    }
    @SuppressLint("SimpleDateFormat")
    private fun saatBul():String{
        val saatFormat=SimpleDateFormat("kk:mm")
        val saat= Date()
        val simdiSaat=saatFormat.format(saat).toString()
//        Log.e("saat",simdiSaat)
        return simdiSaat.toString()
    }
    private fun veritabaniVakitSorgula():Boolean {
        var sonuc=false
        val tarih= tarihBul()
        val vt = DatabaseVakitler(this@MainActivity)
        val vakitlerListe = VakitlerDao().vakitListele(vt)
        if (vakitlerListe.size<=15|| vakitlerListe.isEmpty()){
            listele()
        }
        for (k in vakitlerListe) {

            if (k.miladiTarih==tarih) {
                sonuc=true
                break
            }

        }
//        Log.e("sonuc vt sorgula",sonuc.toString())
        return sonuc
    }
    private fun veritabaniSorgula(){
        val vt= DatabaseKonum(this)
        val konumlarListe= Konumlardao().konumListele(vt)
        for (k in konumlarListe){
            konumId=k.ilceId
            ilceAdi=k.ilce
            sehirAdi=k.sehir
        }

    }
    private fun listele(){


        val retrofitBuilder=Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(NamazInterface::class.java)
        val retrofitData=retrofitBuilder.getData(konumId)
        retrofitData.enqueue(object : Callback<List<NamazJsonItem>>{
            override fun onResponse(
                    call: Call<List<NamazJsonItem>>,
                    response: Response<List<NamazJsonItem>>
            ) {
                val responseBody=response.body()!!
                val vt = DatabaseVakitler(applicationContext)
                val vakitlerListe=VakitlerDao().vakitListele(vt)
                for (k in vakitlerListe){
                    VakitlerDao().vakitSil(vt,k.id)
//                    Log.e("imsak veritabanı L",k.imsak)
                }
                for (r in responseBody) {

                    VakitlerDao().vakitEkle(vt, "${r.Imsak}", "${r.Gunes}",
                            "${r.Ogle}", "${r.Ikindi}", "${r.Aksam}", "${r.Yatsi}",
                            "${r.MiladiTarihKisa}", "${r.HicriTarihKisa}", "${r.AyinSekliURL}")
                    vakitleriAdapterdaGoster()


                }

            }

            override fun onFailure(call: Call<List<NamazJsonItem>>, t: Throwable) {
                Log.e("MainActivity",t.message.toString())
            }
        })
    }

    private fun vakitleriAdapterdaGoster(){
        val tarih=tarihBul()
        val vt = DatabaseVakitler(this)
        val vakitlerListe=VakitlerDao().vakitListele(vt)
        var i=0
        for (k in vakitlerListe){

            if (k.miladiTarih==tarih) {
                imsak=k.imsak
                gunes=k.gunes
                ogle=k.ogle
                ikindi=k.ikindi
                aksam=k.aksam
                yatsi=k.yatsi

//                Log.e("imsak service",imsak)
                break
            }
            i++
        }
        val elemanSayisi=vakitlerListe.size

        val sonListe:ArrayList<Vakitler>
        sonListe= ArrayList()
        for (x in i..elemanSayisi-1){
            sonListe.add(vakitlerListe.get(x))
        }
        if (sonListe.size<15){
            listele()
        }
//        Log.e("sonListe",sonListe.get(5).toString())
        myAdapter= NamazVakitleriAdapter(applicationContext, sonListe)
        myAdapter.notifyDataSetChanged()
        rvAylıkNamazVakitleri.adapter=myAdapter
    }


}