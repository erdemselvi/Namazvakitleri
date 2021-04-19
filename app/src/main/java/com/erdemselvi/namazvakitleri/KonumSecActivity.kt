package com.erdemselvi.namazvakitleri

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.erdemselvi.namazvakitleri.api.NamazInterface
import com.erdemselvi.namazvakitleri.api.NamazJsonItem
import com.erdemselvi.namazvakitleri.database.DatabaseKonum
import com.erdemselvi.namazvakitleri.database.DatabaseVakitler
import com.erdemselvi.namazvakitleri.database.Konumlardao
import com.erdemselvi.namazvakitleri.database.VakitlerDao
import com.erdemselvi.namazvakitleri.konumapi.Konum
import com.erdemselvi.namazvakitleri.konumapi.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_konum_sec.*
import kotlinx.android.synthetic.main.activity_konum_sec.view.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class KonumSecActivity : AppCompatActivity() {

    lateinit var sehirler:ArrayList<String>
    lateinit var sehirId:ArrayList<String>
    lateinit var ilceler:ArrayList<String>
    lateinit var ilceId:ArrayList<String>
    lateinit var ulkeler:ArrayList<String>
    lateinit var ulkeId:ArrayList<String>

    var ulkeid="2"
    var ulkeAdi="TÜRKİYE"
    var sehirid="525"
    var sehirAdi="İSTANBUL"
    var ilceid="9551"
    var ilceAdi="İSTANBUL"

    lateinit var gidecekVeri:ArrayList<Konum>
    var sonuc :Boolean = false

    lateinit var  spUlkeler: Spinner
    lateinit var  spSehirler: Spinner
    lateinit var  spIlceler: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konum_sec)

        spUlkeler=findViewById(R.id.spUlkeler)
        spSehirler=findViewById(R.id.spSehirler)
        spIlceler=findViewById(R.id.spIlceler)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")

        sehirler= ArrayList()
        sehirId= ArrayList()
        ilceId= ArrayList()
        ilceler= ArrayList()
        ulkeler= ArrayList()
        ulkeId= ArrayList()

        ulkeler.add("Türkiye")
        sehirler.add("Diyarbakır")
        ilceler.add("Yenişehir")
        ilceId.add("9541")
        sehirId.add("539")
        ulkeId.add("2")

        supportActionBar?.hide()
//        val vt2 = DatabaseVakitler(this)
//        val vakitlerListe= VakitlerDao().vakitListele(vt2)
//        for (k in vakitlerListe){
//            VakitlerDao().vakitSil(vt2,k.id)
//            Log.e("imsak VT vakit sil",k.imsak)
//        }
//        val gelenSonuc=veritabanıSorgula()
//        Log.e("sonuc",gelenSonuc.toString())
        veritabaniSorgula()
        if (sonuc==true){
            val intent=Intent(this,GunlukVakitActivity::class.java)
            startActivity(intent)
            finish()
        }

//       spSehirler.adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list)
//       spIlceler.adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,sehir)

      ulkeListele()

//        gidecekVeri= ArrayList()
//        val gidecek= Konum(ulkeAdi,sehirAdi,ilceAdi,ilceid)
//        gidecekVeri.add(gidecek)
//        Log.e("gidecekveri",gidecekVeri.toString())
        btGonder.setOnClickListener {

            val vt=DatabaseKonum(this)
            Konumlardao().konumEkle(vt,"${ulkeId[spUlkeler.selectedItemPosition]}","${ulkeler[spUlkeler.selectedItemPosition]}",
                    "${sehirId[spSehirler.selectedItemPosition]}","${sehirler[spSehirler.selectedItemPosition]}",
                    "${ilceId[spIlceler.selectedItemPosition]}","${ilceler[spIlceler.selectedItemPosition]}")
//            val konumlarListe=Konumlardao().konumListele(vt)
//            for (k in konumlarListe){
//                Log.e("idkayit",k.id.toString())
//                Log.e("ulkeidK",k.ulkeId)
//                Log.e("ulkeK",k.ulke)
//                Log.e("sehirIdK",k.sehirId)
//                Log.e("sehirK",k.sehir)
//                Log.e("ilceIdK",k.ilceId)
//                Log.e("ilceK",k.ilce)
//            }
            vakitleriKaydet()
//            val handler=Handler()
//            handler.postDelayed({
//                //doSomethingHere()
//            }, 3000)

        }


        // spUlkeler.adapter= ArrayAdapter(this@KonumSecActivity,android.R.layout.simple_list_item_1,ulke)


    }

    private fun veritabaniSorgula():Boolean {

        val vt = DatabaseKonum(this)
        val konumlarListe = Konumlardao().konumListele(vt)
        sonuc = !konumlarListe.isEmpty()
        return sonuc
    }
    private fun ulkeListele() {
        try {


            val retrofitBuilder2 = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(KonumInterface::class.java)
            val retrofitData2 = retrofitBuilder2.getDataKonum()
            retrofitData2.enqueue(object : Callback<List<KonumJsonItem>> {

                override fun onResponse(
                    call: Call<List<KonumJsonItem>>,
                    response: Response<List<KonumJsonItem>>
                ) {
                    if (response.isSuccessful) {
                        val responseBody1 = response.body()!!
//                    val ulkelerr = HashMap<String, String>()

                        var i = 0
                        for (myData in responseBody1) {
                            ulkeler.add(myData.UlkeAdiEn)
                            ulkeId.add(myData.UlkeID)
//                                ulkelerr.put("ulkeid", myData.UlkeID)
//                                ulkelerr.put("ulkeadi", myData.UlkeAdiEn)

                            //         Log.e("ddd", "${i} - ${ulkeler[i]}")
//                    myStringBuilder.append(myData.UlkeAdi)
//                    myStringBuilder.append("\n")
                            i++
                        }
//                        val database = FirebaseDatabase.getInstance()
//                        val myRef = database.getReference().child("ulkeler")
//                        myRef.push().setValue(ulkeler).addOnFailureListener {
//                            Log.e("firebase", it.toString())
//                        }


                        val adapterr1 = ArrayAdapter(
                            this@KonumSecActivity,
                            android.R.layout.simple_list_item_1,
                            ulkeler
                        )

//                        adapterr1.setDropDownViewResource(android.R.layout.simple_list_item_1)
                        spUlkeler.adapter = adapterr1
//                        val ulke = "TÜRKİYE"
//                        val position = adapterr.getPosition(ulke)
//                        spUlkeler.setSelection(position) //spinnerda varsayalın olarak ilk Türkiye gözüksün (listedeki index numarası)
                        spUlkeler.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    p2: Int,
                                    p3: Long
                                ) {

                                    ulkeid = ulkeId.get(p2)
                                    ulkeAdi = ulkeler.get(p2)

                                        listele()


                                    // Toast.makeText(this@KonumSecActivity,ulke.get(p2),Toast.LENGTH_SHORT)
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                    Log.e("ulkehata", p0.toString())
                                }

                            }
                    }
                }

                override fun onFailure(call: Call<List<KonumJsonItem>>, t: Throwable) {
                    Log.e("KonumSecActivity", t.message.toString())
                }
            })
        }catch (e:Exception){
            Log.e("ulketry",e.toString())
        }
    }

    private fun listele(){
        try {


            sehirler.clear()
            sehirId.clear()
            val retrofitBuilder2 = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(SehirlerInterface::class.java)
            val retrofitData2 = retrofitBuilder2.getDataSehirler(ulkeid.toString())
            retrofitData2.enqueue(object : Callback<List<SehirlerJsonItem>> {
                override fun onResponse(
                    call: Call<List<SehirlerJsonItem>>,
                    response: Response<List<SehirlerJsonItem>>
                ) {
                    val responseBody = response.body()!!


                    for (myData in responseBody) {
                        sehirler.add(myData.SehirAdiEn)
                        sehirId.add(myData.SehirID)
//                    myStringBuilder.append(myData.UlkeAdi)
//                    myStringBuilder.append("\n")
                    }

                    val adapter = ArrayAdapter(
                        this@KonumSecActivity,
                        android.R.layout.simple_list_item_1,
                        sehirler
                    )
                    //                       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spSehirler.adapter = adapter

                    spSehirler.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {

                                sehirid = sehirId.get(p2)
                                sehirAdi = sehirler.get(p2)

                                    ilcelerilistele()

                                // Toast.makeText(this@KonumSecActivity,ulke.get(p2),Toast.LENGTH_SHORT)
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }

                        }
                }

                override fun onFailure(call: Call<List<SehirlerJsonItem>>, t: Throwable) {
                    Log.e("KonumSecActivity", t.message.toString())
                }
            })
        }catch (e:Exception){
            Log.e("iltry",e.toString())
        }
    }

    private fun ilcelerilistele() {
        try {


            ilceler.clear()
            ilceId.clear()
            val retrofitBuilder2 = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(IlcelerInterface::class.java)
            val retrofitData2 = retrofitBuilder2.getDataIlceler(sehirid)
            retrofitData2.enqueue(object : Callback<IlcelerJson> {
                override fun onResponse(
                    call: Call<IlcelerJson>,
                    response: Response<IlcelerJson>
                ) {
                    val responseBody = response.body()!!


                    for (myData in responseBody) {
                        ilceler.add(myData.IlceAdiEn)
                        ilceId.add(myData.IlceID)
//                    myStringBuilder.append(myData.UlkeAdi)
//                    myStringBuilder.append("\n")
                    }

                    val adapter = ArrayAdapter(
                        this@KonumSecActivity,
                        android.R.layout.simple_list_item_1,
                        ilceler
                    )
                    //                       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spIlceler.adapter = adapter

                    spIlceler.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {

                                ilceid = ilceId.get(p2)
                                ilceAdi = ilceler.get(p2)


//                        gidecekVeri.add(ulkeAdi)
//                        gidecekVeri.add(sehirAdi)
//                        gidecekVeri.add(ilceAdi)
//                        gidecekVeri.add(ilceid)


                                // Toast.makeText(this@KonumSecActivity,ulke.get(p2),Toast.LENGTH_SHORT)
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }

                        }
                }

                override fun onFailure(call: Call<IlcelerJson>, t: Throwable) {
                    Log.e("KonumSecActivity", t.message.toString())
                }
            })
        }catch (e:Exception){
            Log.e("ilcetry",e.toString())
        }
    }

    private fun vakitleriKaydet() {
        progressBarYukleniyor.visibility=View.VISIBLE
        tvYukleniyor.visibility=View.VISIBLE
        try {


            val vt2 = DatabaseVakitler(this)
            val vakitlerListe = VakitlerDao().vakitListele(vt2)
            for (k in vakitlerListe) {
                VakitlerDao().vakitSil(vt2, k.id)
                Log.e("imsak VT vakit sil", k.imsak)
            }

            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(NamazInterface::class.java)
            val retrofitData = retrofitBuilder.getData(ilceId[spIlceler.selectedItemPosition])
            retrofitData.enqueue(object : Callback<List<NamazJsonItem>> {
                override fun onResponse(
                    call: Call<List<NamazJsonItem>>,
                    response: Response<List<NamazJsonItem>>
                ) {
                    val responseBody = response.body()!!
                    //namazListe.add(responseBody.first())

//                for (r in responseBody){
//                    imsak=r.Imsak
//                    gunes=r.Gunes
//                    ogle=r.Ogle
//                    ikindi=r.Ikindi
//                    aksam=r.Aksam
//                    yatsi=r.Yatsi
//                    break
//                }
                    val vt = DatabaseVakitler(this@KonumSecActivity)
                    for (r in responseBody) {
                        VakitlerDao().vakitEkle(
                            vt, "${r.Imsak}", "${r.Gunes}",
                            "${r.Ogle}", "${r.Ikindi}", "${r.Aksam}", "${r.Yatsi}",
                            "${r.MiladiTarihKisa}", "${r.HicriTarihKisa}", "${r.AyinSekliURL}"
                        )
                    }
                    val vakitliste = VakitlerDao().vakitListele(vt)
                    for (v in vakitliste) {
                        Log.e("imsak vt ilk Kyt", v.imsak)
                    }
                    Log.e("vakit kayıt", responseBody.toString())
                    val intent = Intent(applicationContext, GunlukVakitActivity::class.java)
                    //  intent.putExtra("konum",gidecekVeri)
//            intent.putExtra("ilceId",ilceId[spIlceler.selectedItemPosition])
//            intent.putExtra("ilceAdi",ilceler[spIlceler.selectedItemPosition])
//            intent.putExtra("ilAdi",sehirler[spSehirler.selectedItemPosition])

                    Log.e("ilceID", ilceId[spIlceler.selectedItemPosition])
                    Log.e("ilceAdı", ilceler[spIlceler.selectedItemPosition])
                    Log.e("ilAdı", sehirler[spSehirler.selectedItemPosition])
                    progressBarYukleniyor.visibility=View.GONE
                    tvYukleniyor.visibility=View.GONE
                    startActivity(intent)
                    finish()

                }

                override fun onFailure(call: Call<List<NamazJsonItem>>, t: Throwable) {
                    Log.e("MainActivity", t.message.toString())
                }
            })

        }catch (e:Exception){
            Log.e("vakittry",e.toString())

        }
    }
}

