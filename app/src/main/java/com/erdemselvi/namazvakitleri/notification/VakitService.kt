package com.erdemselvi.namazvakitleri.notification

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import com.erdemselvi.namazvakitleri.BASE_URL
import com.erdemselvi.namazvakitleri.GunlukVakitActivity
import com.erdemselvi.namazvakitleri.MainActivity
import com.erdemselvi.namazvakitleri.R
import com.erdemselvi.namazvakitleri.api.NamazInterface
import com.erdemselvi.namazvakitleri.api.NamazJsonItem
import com.erdemselvi.namazvakitleri.database.DatabaseKonum
import com.erdemselvi.namazvakitleri.database.DatabaseVakitler
import com.erdemselvi.namazvakitleri.database.Konumlardao
import com.erdemselvi.namazvakitleri.database.VakitlerDao
import com.erdemselvi.namazvakitleri.notification.App.Companion.CHANNEL_ID
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class VakitService: Service() {

    lateinit var imsak:String
    lateinit var yarinkiImsak:String
    lateinit var gunes:String
    lateinit var ogle:String
    lateinit var ikindi:String
    lateinit var aksam:String
    lateinit var yatsi:String
    lateinit var mTarih:String

    lateinit var konumId:String
    lateinit var ilceAdi:String
    lateinit var sehirAdi:String
    lateinit var sonrakiVakit:String

    lateinit var sonrakiVakitAdi:String
    var donenZaman=""

    override fun onCreate() {
        super.onCreate()
        veritabanıSorgula2()
        foregrounServiceBaslat()

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        sayacBaslat()

        return START_STICKY ;
    }

    @Nullable
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun sayacBaslat(){

        object : CountDownTimer(60000,1000){

            override fun onTick(p0: Long) {

                if ((tarihBul()!=mTarih || saatBul()==imsak || saatBul()==gunes || saatBul()==ogle ||
                                saatBul()==ikindi || saatBul()==aksam || saatBul()==yatsi||saniyeBul()==0)) {


                    foregrounServiceBaslat()

                }
//                if (gunBul()=="28"){
//                    listeleVakit()
//                }
            }

            override fun onFinish() {
                foregrounServiceBaslat()
                sayacBaslat()
            }
        }.start()


    }
    private fun foregrounServiceBaslat(){

        veritabanıVakitListele()


        val view = RemoteViews(getPackageName(), R.layout.foreground_service_vakitler)

        view.setTextViewText(R.id.tvfImsak, imsak)
        view.setTextViewText(R.id.tvfGunes, gunes)
        view.setTextViewText(R.id.tvfOgle, ogle)
        view.setTextViewText(R.id.tvfIkindi, ikindi)
        view.setTextViewText(R.id.tvfAksam, aksam)
        view.setTextViewText(R.id.tvfYatsi, yatsi)

        val simdiSaat=saatBul()
//        Log.e("simdiki saat",simdiSaat)
        val sd=simdiSaat.split(":")
        val sdImsak=imsak.split(":")
        val sdGunes=gunes.split(":")
        val sdOgle=ogle.split(":")
        val sdIkindi=ikindi.split(":")
        val sdAksam=aksam.split(":")
        val sdYatsi=yatsi.split(":")
        veritabanıYarinkiImsakListele()
//        val sdYarinkiImsak=yarinkiImsak.split(":")

        if (saatBul()==imsak ||(sd[0].toInt()>sdImsak[0].toInt() && sd[0].toInt()<sdGunes[0].toInt()) ||
                ( sd[0].toInt()==sdImsak[0].toInt() && sd[1].toInt()>=sdImsak[1].toInt()) ||  (sd[0].toInt()==sdGunes[0].toInt() && sd[1].toInt()<sdGunes[1].toInt()))

        {
            sonrakiVakit=gunes
            sonrakiVakitAdi="Güneşe"

            view.setTextColor(R.id.tvfImsak, Color.RED)
            view.setTextColor(R.id.tvfYatsi, Color.BLACK)
            view.setTextColor(R.id.textViewI, Color.RED)
            view.setTextColor(R.id.textViewY, Color.BLACK)
        }
        else if (saatBul()==gunes ||(sd[0].toInt()>sdGunes[0].toInt() && sd[0].toInt()<sdOgle[0].toInt()) ||
                ( sd[0].toInt()==sdGunes[0].toInt() && sd[1].toInt()>=sdGunes[1].toInt()) ||
                (sd[0].toInt()==sdOgle[0].toInt() && sd[1].toInt()<sdOgle[1].toInt()))
        {
            sonrakiVakit=ogle
            sonrakiVakitAdi="Öğlene"

            view.setTextColor(R.id.tvfGunes, Color.RED)
            view.setTextColor(R.id.tvfImsak, Color.BLACK)
            view.setTextColor(R.id.textViewG, Color.RED)
            view.setTextColor(R.id.textViewI, Color.BLACK)
        }
        else if (saatBul()==ogle ||(sd[0].toInt()>sdOgle[0].toInt() && sd[0].toInt()<sdIkindi[0].toInt()) ||
                ( sd[0].toInt()==sdOgle[0].toInt() && sd[1].toInt()>=sdOgle[1].toInt()) ||
                (sd[0].toInt()==sdIkindi[0].toInt() && sd[1].toInt()<sdIkindi[1].toInt()))
        {
            sonrakiVakit=ikindi
            sonrakiVakitAdi="İkindiye"

            view.setTextColor(R.id.tvfOgle, Color.RED)
            view.setTextColor(R.id.tvfGunes, Color.BLACK)
            view.setTextColor(R.id.textViewO, Color.RED)
            view.setTextColor(R.id.textViewG, Color.BLACK)
        }
        else if (saatBul()==ikindi ||(sd[0].toInt()>sdIkindi[0].toInt() && sd[0].toInt()<sdAksam[0].toInt()) ||
                ( sd[0].toInt()==sdIkindi[0].toInt() && sd[1].toInt()>=sdIkindi[1].toInt()) ||
                (sd[0].toInt()==sdAksam[0].toInt() && sd[1].toInt()<sdAksam[1].toInt()))
        {
            sonrakiVakit=aksam
            sonrakiVakitAdi="Akşama"

            view.setTextColor(R.id.tvfIkindi, Color.RED)
            view.setTextColor(R.id.tvfOgle, Color.BLACK)
            view.setTextColor(R.id.textViewIk, Color.RED)
            view.setTextColor(R.id.textViewO, Color.BLACK)
        }
        else if (saatBul()==aksam ||(sd[0].toInt()>sdAksam[0].toInt() && sd[0].toInt()<sdYatsi[0].toInt()) ||
                ( sd[0].toInt()==sdAksam[0].toInt() && sd[1].toInt()>=sdAksam[1].toInt())
                ||  (sd[0].toInt()==sdYatsi[0].toInt() && sd[1].toInt()<sdYatsi[1].toInt()))
        {
            sonrakiVakit=yatsi
            sonrakiVakitAdi="Yatsıya"
            view.setTextColor(R.id.tvfAksam, Color.RED)
            view.setTextColor(R.id.tvfIkindi, Color.BLACK)
            view.setTextColor(R.id.textViewA, Color.RED)
            view.setTextColor(R.id.textViewIk, Color.BLACK)
        }
        else if (saatBul()==yatsi ||(sd[0].toInt()>sdYatsi[0].toInt() && sd[0].toInt()<24) ||
                ( sd[0].toInt()==sdYatsi[0].toInt() && sd[1].toInt()>=sdYatsi[1].toInt()))
        {
            sonrakiVakit=yarinkiImsak
            sonrakiVakitAdi="İmsaka"

            view.setTextColor(R.id.tvfYatsi, Color.RED)
            view.setTextColor(R.id.tvfAksam, Color.BLACK)
            view.setTextColor(R.id.textViewY, Color.RED)
            view.setTextColor(R.id.textViewA, Color.BLACK)
        }
        else if (sd[0].toInt()<sdImsak[0].toInt() || sd[0].toInt()==24 ||
                ( sd[0].toInt()==sdImsak[0].toInt() && sd[1].toInt()<sdImsak[1].toInt()))
        {
            sonrakiVakit=imsak
            sonrakiVakitAdi="İmsaka"

            view.setTextColor(R.id.tvfImsak, Color.RED)
            view.setTextColor(R.id.tvfYatsi, Color.BLACK)
            view.setTextColor(R.id.textViewI, Color.RED)
            view.setTextColor(R.id.textViewY, Color.BLACK)
        }
        vakteKalanZamaniBul(sonrakiVakit,sonrakiVakitAdi)
        view.setTextViewText(R.id.tvKalanSureGoster,"$sonrakiVakitAdi: ")
        view.setTextViewText(R.id.tvKalanSure, donenZaman.toString())
        view.setTextViewText(R.id.tvServisKonum,ilceAdi)
        val notificationIntent = Intent(this, GunlukVakitActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                //.setContentTitle("Namaz Vakitleri burada")
                // .setContentText(input)
                .setCustomContentView(view)
                .setCustomBigContentView(view)
                .setSmallIcon(R.mipmap.ic_launcher_nv)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build()

        startForeground(1, notification)


    }
    private fun veritabanıSorgula2(){
        val vt= DatabaseKonum(this)
        val konumlarListe= Konumlardao().konumListele(vt)
        for (k in konumlarListe){
            konumId=k.ilceId
            ilceAdi=k.ilce
            sehirAdi=k.sehir

        }


    }
    private fun tarihBul():String {

        val tarihFormat= SimpleDateFormat("dd.MM.yyyy")
        val tarih= Date()
        val simdiTarih=tarihFormat.format(tarih)

        return simdiTarih.toString()

    }
    private fun gunBul():String {

        val tarihFormat= SimpleDateFormat("dd")
        val tarih= Date()
        val gunTarih=tarihFormat.format(tarih)

        return gunTarih.toString()

    }
    private fun tarihBulYarin():String {

        val tarihFormat= SimpleDateFormat("dd.MM.yyyy")
        val tarih= Date()
        val tarih2=Date(tarih.time+(24 * 60 * 60 * 1000))
        val yarinkiTarih=tarihFormat.format(tarih2)


        return yarinkiTarih.toString()

    }
    private fun saatBul():String{
        val saatFormat= SimpleDateFormat("kk:mm")
        val saat= Date()
        val simdiSaat=saatFormat.format(saat).toString()

        return simdiSaat
    }
    private fun saniyeBul():Int{
        val saniyeFormat= SimpleDateFormat("ss")
        val saniye= Date()
        val simdiSaniye=saniyeFormat.format(saniye)

        return simdiSaniye.toInt()
    }
    private fun vakteKalanZamaniBul(sonrakiVakit:String,sonrakiVakitAdi: String){
        val sv=sonrakiVakit.split(":")
        val simdikiZaman=saatBul().split(":")

        if (sonrakiVakitAdi!="İmsaka"){

            var kalanSaat=sv[0].toInt()-simdikiZaman[0].toInt()
            var kalanDakika= abs(sv[1].toInt()-simdikiZaman[1].toInt())
            if (simdikiZaman[1].toInt()>sv[1].toInt()){
                kalanDakika=60-simdikiZaman[1].toInt()+sv[1].toInt()
                if (sv[0].toInt()-simdikiZaman[0].toInt()!=0){
                    kalanSaat= sv[0].toInt()-simdikiZaman[0].toInt()-1   }
            }

            if (kalanSaat==0){
                donenZaman = "$kalanDakika dk"
            }else if(kalanDakika==0){
                donenZaman = "$kalanSaat s"
            }
            else {
                donenZaman = "$kalanSaat s $kalanDakika dk"
            }

        }else{
            val yatzi=yatsi.split(":")

            if(simdikiZaman[0].toInt()>=yatzi[0].toInt() && simdikiZaman[0].toInt()<24 ) {
                val yarImZ = yarinkiImsak.split(":")
                Log.e("24den küçük",simdikiZaman[0])
                var kalanSaat = (24-simdikiZaman[0].toInt()  ) + yarImZ[0].toInt()
                var kalanDakika= abs(simdikiZaman[1].toInt()-yarImZ[1].toInt())
                if (simdikiZaman[1].toInt()>yarImZ[1].toInt()){
                    kalanDakika=60-simdikiZaman[1].toInt()+yarImZ[1].toInt()

                    kalanSaat= kalanSaat-1   }


                if (kalanSaat==0){
                    donenZaman = "$kalanDakika dk"
                }
                else if(kalanDakika==0){
                    donenZaman = "$kalanSaat s"
                }
                else {
                    donenZaman = "$kalanSaat s $kalanDakika dk"
                }

            }
            else if(simdikiZaman[0].toInt()==24){


                val sdImsakk=imsak.split(":")

                var kalanSaat =sdImsakk[0].toInt()
                var kalanDakika= abs(simdikiZaman[1].toInt()-sdImsakk[1].toInt())
                if (simdikiZaman[1].toInt()>sdImsakk[1].toInt()){
                    kalanDakika=60-simdikiZaman[1].toInt()+sdImsakk[1].toInt()

                    kalanSaat= sdImsakk[0].toInt()-1   }

                if (kalanSaat==0){
                    donenZaman = "$kalanDakika dk"
                }
                else if(kalanDakika==0){
                    donenZaman = "$kalanSaat s"
                }
                else {
                    donenZaman = "$kalanSaat s $kalanDakika dk"
                }
            }
            else if (simdikiZaman[0].toInt()>=1){
                val sdImsakk=imsak.split(":")
                var kalanSaat=sdImsakk[0].toInt()-simdikiZaman[0].toInt()
                var kalanDakika=abs(sdImsakk[1].toInt()-simdikiZaman[1].toInt())
                if (simdikiZaman[1].toInt()>sdImsakk[1].toInt()){
                    kalanDakika=60-simdikiZaman[1].toInt()+sdImsakk[1].toInt()
                    if ((sdImsakk[0].toInt()-simdikiZaman[0].toInt())!=0){
                        kalanSaat= (sdImsakk[0].toInt()-simdikiZaman[0].toInt())-1   }
                }
                if (kalanSaat==0){
                    donenZaman = "$kalanDakika dk"
                }
                else if(kalanDakika==0){
                    donenZaman = "$kalanSaat s"
                }
                else {
                    donenZaman = "$kalanSaat s $kalanDakika dk"
                }
            }
        }
    }
    private fun veritabanıVakitListele() {
        var sonuc=false
        veritabanıSorgula2()
        val tarih=tarihBul()
        val vt = DatabaseVakitler(this)
        val vakitlerListe = VakitlerDao().vakitListele(vt)
        if (vakitlerListe.size<=15|| vakitlerListe.isEmpty()){
            listeleVakit()
        }
        for (k in vakitlerListe) {
            if (k.miladiTarih==tarih) {
                imsak=k.imsak
                gunes=k.gunes
                ogle=k.ogle
                ikindi=k.ikindi
                aksam=k.aksam
                yatsi=k.yatsi
                mTarih=k.miladiTarih
                sonuc=true

                break

            }

        }
        if (sonuc==false){
            listeleVakit()
        }

    }
    private fun veritabanıYarinkiImsakListele() {
        var sonuc=false
        veritabanıSorgula2()

        val vt = DatabaseVakitler(this)
        val vakitlerListe = VakitlerDao().vakitListele(vt)
        for (k in vakitlerListe) {
            if (tarihBulYarin()==k.miladiTarih){

                yarinkiImsak=k.imsak

                sonuc=true

                break

            }

        }
        if (sonuc==false){
            listeleVakit()
        }

    }
    private fun listeleVakit(){
        veritabanıSorgula2()
        val retrofitBuilder= Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(NamazInterface::class.java)
        val retrofitData=retrofitBuilder.getData(konumId)
        retrofitData.enqueue(object : Callback<List<NamazJsonItem>> {
            override fun onResponse(
                    call: Call<List<NamazJsonItem>>,
                    response: Response<List<NamazJsonItem>>
            ) {
                val responseBody=response.body()!!

                for (r in responseBody){
                    imsak=r.Imsak
                    gunes=r.Gunes
                    ogle=r.Ogle
                    ikindi=r.Ikindi
                    aksam=r.Aksam
                    yatsi=r.Yatsi
                    break
                }
                Log.e("namazListes",imsak+gunes)

                val vt = DatabaseVakitler(applicationContext)
                val vakitlerListe=VakitlerDao().vakitListele(vt)
                for (k in vakitlerListe){
                    VakitlerDao().vakitSil(vt,k.id)
                    Log.e("imsak veritabanı L",k.imsak)
                }
                for (r in responseBody) {

                    VakitlerDao().vakitEkle(vt, "${r.Imsak}", "${r.Gunes}",
                            "${r.Ogle}", "${r.Ikindi}", "${r.Aksam}", "${r.Yatsi}",
                            "${r.MiladiTarihKisa}", "${r.HicriTarihKisa}", "${r.AyinSekliURL}")
                    veritabanıVakitListele()


                }


            }

            override fun onFailure(call: Call<List<NamazJsonItem>>, t: Throwable) {
                Log.e("VakitService",t.message.toString())
            }
        })
    }
}