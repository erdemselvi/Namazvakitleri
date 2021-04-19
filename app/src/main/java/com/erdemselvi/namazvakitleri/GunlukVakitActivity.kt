package com.erdemselvi.namazvakitleri

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.erdemselvi.namazvakitleri.api.NamazInterface
import com.erdemselvi.namazvakitleri.api.NamazJsonItem
import com.erdemselvi.namazvakitleri.database.DatabaseKonum
import com.erdemselvi.namazvakitleri.database.DatabaseVakitler
import com.erdemselvi.namazvakitleri.database.Konumlardao
import com.erdemselvi.namazvakitleri.database.VakitlerDao
import com.erdemselvi.namazvakitleri.notification.VakitService
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_gunluk_vakit.*
import kotlinx.android.synthetic.main.activity_gunluk_vakit.swBildirimCubugu
import kotlinx.android.synthetic.main.activity_gunluk_vakit.tvbildirimCubugu
import kotlinx.android.synthetic.main.activity_gunluk_vakit.view.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.aylik_vakitler_tasarim.*
import kotlinx.android.synthetic.main.hakkinda.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class GunlukVakitActivity : AppCompatActivity() {

    private lateinit var timer: CountDownTimer

    lateinit var toggle: ActionBarDrawerToggle

    lateinit var yarinkiImsak:String
    lateinit var yarinkiG:String
    lateinit var yarinkiO:String
    lateinit var yarinkiIk:String
    lateinit var yarinkiA:String
    lateinit var yarinkiY:String
    lateinit var imsak:String
    lateinit var gunes:String
    lateinit var ogle:String
    lateinit var ikindi:String
    lateinit var aksam:String
    lateinit var yatsi:String
    lateinit var mTarih:String
    lateinit var hTarih:String
    lateinit var ayUrl: String

    lateinit var konumId:String
    lateinit var ilceAdi:String
    lateinit var sehirAdi:String

    lateinit var sonrakiVakit:String
    lateinit var sonrakiVakitAdi:String
    lateinit var tvAdi:TextView

    lateinit var endTime:String
    lateinit var endTimeY:String
    lateinit var enDate:Date
    lateinit var enDateY:Date
    lateinit var endTimeA:String
    lateinit var endTimeIk:String
    lateinit var enDateA:Date
    lateinit var enDateIk:Date
    lateinit var endTimeO:String
    lateinit var endTimeG:String
    lateinit var enDateO:Date
    lateinit var enDateG:Date
    lateinit var endTimeI:String
    lateinit var enDateI:Date
    lateinit var enDateRmznGecen:Date
    lateinit var endTimeRmznGecen:String
    lateinit var enDateRmznKalan:Date
    lateinit var endTimeRmznKalan:String
    var miliseconds:Long=0
    var milisecondsY:Long=0
    var milisecondsA:Long=0
    var milisecondsIk:Long=0
    var milisecondsO:Long=0
    var milisecondsG:Long=0
    var milisecondsI:Long=0
    var milisecondsRmznGecen:Long=0
    var milisecondsRmznKalan:Long=0

    var progressMax:Long=0
    var currentProgress:Long=0

    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gunluk_vakit)
        //reklamlar yükleniyor
        bannerReklamYukle()
        interstitalReklamYukle() //geçiş reklamı


        veritabaniSorgula()

        toggle= ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        toolbarYukle()


        navView.setNavigationItemSelectedListener {navItem->
            if(navItem.itemId== R.id.nav_item_vakitler){
                timer.cancel()
                veritabaniSorgula()
                veritabanıVakitListele()
                vakitKontrol()
                kalanZamanHesabi(endTime)
                toolbarYukle()
                zamaniBaslat()

            }
            if(navItem.itemId== R.id.nav_item_aylik_vakitler){

                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                val yeniIntent = Intent(this,MainActivity::class.java)
                startActivity(yeniIntent)

            }
            if(navItem.itemId== R.id.nav_item_il_degistir){
                val alertDegistir=AlertDialog.Builder(this)
                alertDegistir.setTitle("Konum Değiştir")
                alertDegistir.setMessage("${ilceAdi} konumunu değiştirmek istiyormusunuz?")
                alertDegistir.setIcon(R.drawable.ic_baseline_cached_24)

                alertDegistir.setPositiveButton("Evet"){_, _ ->
                    val vt= DatabaseKonum(this)
                    val konumlarListe= Konumlardao().konumListele(vt)
                    for (k in konumlarListe){
                        Konumlardao().konumSil(vt,k.id)
                    }
                    val serviceIntent= Intent(this, VakitService::class.java)
                    stopService(serviceIntent)
                    val intent=Intent(this,KonumSecActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                alertDegistir.setNegativeButton("Hayır"){_, _ ->

                }
                alertDegistir.create().show()



            }
            if(navItem.itemId== R.id.nav_item_hakkinda){
                val mPopupHakkinda= LayoutInflater.from(this).inflate(R.layout.hakkinda,null)
                val mBuilder= AlertDialog.Builder(this)
                        .setView(mPopupHakkinda)
                        .setTitle("Uygulama Hakkında")
                mPopupHakkinda.tvHakkinda.text="Namaz Vakitleri Versiyon "+BuildConfig.VERSION_NAME+
                        "\n İletişim: erdemselvi@gmail.com"
                mBuilder.setView(mPopupHakkinda)
                mBuilder.setNegativeButton("Kapat", DialogInterface.OnClickListener { _, _ ->

                })
                mBuilder.show()
            }

            drawer.closeDrawer(GravityCompat.START)
            true
        }

        vakitSorgula()
        veritabanıVakitListele()
        listele()
        vakitleriListele()
        vakitKontrol()
        kalanZamanHesabi(endTime)
        zamaniBaslat()

        if (isMyServiceRunning(VakitService::class.java)){ //servis çalışıyorsa switch true yoksa false olacak
            swBildirimCubugu.isChecked=true
            tvbildirimCubugu.setBackgroundResource(R.drawable.gradient_green)
        }
        else{
            tvbildirimCubugu.setBackgroundColor(Color.GRAY)
            swBildirimCubugu.isChecked=false
        }
        swBildirimCubugu.setOnCheckedChangeListener { _, b -> //switch true olursa service açılsın, yoksa kapansın
            if (b){
                tvbildirimCubugu.setBackgroundResource(R.drawable.gradient_green)
                val serviceIntent= Intent(this, VakitService::class.java)
                ContextCompat.startForegroundService(this,serviceIntent)
            }
            else{
                tvbildirimCubugu.setBackgroundColor(Color.GRAY)
                val serviceIntent= Intent(this, VakitService::class.java)
                stopService(serviceIntent)
            }

        }

    }
    private fun toolbarYukle(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setIcon(R.mipmap.ic_launcher_nv)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setSubtitle(ilceAdi+" - "+ tarihBul())
    }
    private fun interstitalReklamYukle() {
        var adRequest = AdRequest.Builder().build()
//test id="ca-app-pub-3940256099942544/1033173712"
        InterstitialAd.load(this, getString(R.string.interstitial_admob_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("Reklam Hatası", adError.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.e("Reklam", "Reklam yüklendi")
                mInterstitialAd = interstitialAd
            }
        })
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }

    }

    private fun bannerReklamYukle(){
        MobileAds.initialize(this)
        val adRequest=AdRequest.Builder().build()
        adViewGunlukVakit.loadAd(adRequest)
        adViewGunlukVakit.visibility=View.GONE

        adViewGunlukVakit.adListener=object :AdListener(){
            override fun onAdLoaded() {
                adViewGunlukVakit.visibility=View.VISIBLE
                super.onAdLoaded()
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun vakitleriListele() {
        tvI.text=imsak
        tvG.text=gunes
        tvO.text=ogle
        tvIk.text=ikindi
        tvA.text=aksam
        tvY.text=yatsi

        tvHicriTarihh.text=hTarih

        Picasso.with(this)
                .load(ayUrl)

                .resize(150, 150)         //optional
                .centerCrop()                        //optional
                .into(ivAyGorunumm)
    }

    fun isMyServiceRunning(serviceClass : Class<*> ) : Boolean{ //Servis çalışıyormu çalışmıyormu?
        var manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name.equals(service.service.className)) {
                return true
            }
        }
        return false
    }

    private fun zamaniBaslat(){

        var startTime=System.currentTimeMillis()
        var gecenTime=System.currentTimeMillis()
//        val diff=miliseconds-startTime
        var rmznGecen:String?
        var rmznKalan:String?=""

//        Log.e("startime",startTime.toString())

        timer=  object : CountDownTimer(miliseconds,1000){

            override fun onTick(millisUntilFinished: Long) {

//                ObjectAnimator.ofInt(progressBarI,"progressImsak",currentProgressBar)
//                    .setDuration(1000)
//                    .start()

                startTime=startTime-1
                gecenTime=gecenTime+1
//                Log.e("milisecondsGecentime",(millisUntilFinished +milisecondsRmznGecen).toString())
                var  serverUptimeSeconds = (millisUntilFinished - startTime) / 1000

//                var daysLeft = String.format("%d", serverUptimeSeconds / 86400)
                var hoursLeft = String.format("%02d", (serverUptimeSeconds % 86400) / 3600)
                var minutesLeft = String.format("%02d", ((serverUptimeSeconds % 86400) % 3600) / 60)
                var secondsLeft = String.format("%02d", ((serverUptimeSeconds % 86400) % 3600) % 60)
                if (startTime<=millisUntilFinished +milisecondsRmznGecen) {
                    var serverUptimeSecondsRmznGecen =
                            (millisUntilFinished + milisecondsRmznGecen - startTime) / 1000
                    var daysLeftRmznGecen =
                            String.format("%d", serverUptimeSecondsRmznGecen / 86400)
                    var hoursLeftRmznGecen =
                            String.format("%02d", (serverUptimeSecondsRmznGecen % 86400) / 3600)
                    var minutesLeftRmznGecen =
                            String.format("%02d", ((serverUptimeSecondsRmznGecen % 86400) % 3600) / 60)
//                    var secondsLeftRmznGecen =
//                            String.format("%02d", ((serverUptimeSecondsRmznGecen % 86400) % 3600) % 60)
                    rmznGecen ="Ramazan'a "+
                            daysLeftRmznGecen + ":" + hoursLeftRmznGecen + ":" + minutesLeftRmznGecen
                }
                else{
                    gecenTime=gecenTime+1
                    var serverUptimeSecondsRmznGecen =
                            ( gecenTime-(milisecondsRmznGecen+millisUntilFinished)) / 1000
                    var daysLeftRmznGecen =
                            String.format("%d", serverUptimeSecondsRmznGecen / 86400)
                    var hoursLeftRmznGecen =
                            String.format("%02d", (serverUptimeSecondsRmznGecen % 86400) / 3600)
                    var minutesLeftRmznGecen =
                            String.format("%02d", ((serverUptimeSecondsRmznGecen % 86400) % 3600) / 60)
//                    var secondsLeftRmznGecen =
//                        String.format("%02d", ((serverUptimeSecondsRmznGecen % 86400) % 3600) % 60)
                    rmznGecen ="Ramazan Ayı-> biten "+
                            daysLeftRmznGecen + ":" + hoursLeftRmznGecen + ":" + minutesLeftRmznGecen
                }
                if(startTime<=millisUntilFinished+milisecondsRmznKalan) {
                    var serverUptimeSecondsRmznKalan =
                            (millisUntilFinished + milisecondsRmznKalan - startTime) / 1000
                    var daysLeftRmznKalan =
                            String.format("%d", serverUptimeSecondsRmznKalan / 86400)
                    var hoursLeftRmznKalan =
                            String.format("%02d", (serverUptimeSecondsRmznKalan % 86400) / 3600)
                    var minutesLeftRmznKalan =
                            String.format("%02d", ((serverUptimeSecondsRmznKalan % 86400) % 3600) / 60)
//                    var secondsLeftRmznKalan =
//                        String.format("%02d", ((serverUptimeSecondsRmznKalan % 86400) % 3600) % 60)
                    rmznKalan =
                            daysLeftRmznKalan + ":" + hoursLeftRmznKalan + ":" + minutesLeftRmznKalan
                }
                else{
                    tvSimdikiVakitAdi.visibility=View.GONE
                }

                tvSimdikiVakitAdi.text=rmznGecen+", kalan "+rmznKalan

                if (sonrakiVakitAdi=="Yatsı") {

                    progressBarI.visibility=View.INVISIBLE
                    progressBarG.visibility=View.INVISIBLE
                    progressBarO.visibility=View.INVISIBLE
                    progressBarIk.visibility=View.INVISIBLE
                    progressBarA.visibility=View.INVISIBLE
                    progressBarY.visibility=View.VISIBLE

                    progressBarY.max = milisecondsA.toInt()
                    val currentProgressBar = (gecenTime - enDateA.time).toInt()
//                    progressBarI.progress=currentProgressBar
                    ObjectAnimator.ofInt(progressBarY, "progress", currentProgressBar)
                        .setDuration(1000)
                        .start()
                    progressBarY.progress=(gecenTime - enDateA.time).toInt()
                }

                if (sonrakiVakitAdi=="Akşam"){

                    progressBarI.visibility=View.INVISIBLE
                    progressBarG.visibility=View.INVISIBLE
                    progressBarO.visibility=View.INVISIBLE
                    progressBarIk.visibility=View.INVISIBLE
                    progressBarA.visibility=View.VISIBLE
                    progressBarY.visibility=View.INVISIBLE

                    yatsiZamanHesabi(millisUntilFinished,startTime)

                    progressBarA.max= milisecondsIk.toInt()
                    val currentProgressBar=(gecenTime-enDateIk.time).toInt()
//                    progressBarI.progress=currentProgressBar
                    ObjectAnimator.ofInt(progressBarA,"progress",currentProgressBar)
                        .setDuration(1000)
                        .start()
                    progressBarA.progress=(gecenTime-enDateIk.time).toInt()

                }
                else if (sonrakiVakitAdi=="İkindi"){

                    progressBarI.visibility=View.INVISIBLE
                    progressBarG.visibility=View.INVISIBLE
                    progressBarO.visibility=View.INVISIBLE
                    progressBarIk.visibility=View.VISIBLE
                    progressBarA.visibility=View.INVISIBLE
                    progressBarY.visibility=View.INVISIBLE

                    aksamZamanHesabi(millisUntilFinished, startTime)
                    yatsiZamanHesabi(millisUntilFinished, startTime)

                    progressBarIk.progress=(gecenTime-enDateO.time).toInt()
                    progressBarIk.max= milisecondsO.toInt()
                    val currentProgressBar=(gecenTime-enDateO.time).toInt()
//                    progressBarI.progress=currentProgressBar
                    ObjectAnimator.ofInt(progressBarIk,"progress",currentProgressBar)
                        .setDuration(1000)
                        .start()


                }
                else if (sonrakiVakitAdi=="Öğle"){

                    progressBarI.visibility=View.INVISIBLE
                    progressBarG.visibility=View.INVISIBLE
                    progressBarO.visibility=View.VISIBLE
                    progressBarIk.visibility=View.INVISIBLE
                    progressBarA.visibility=View.INVISIBLE
                    progressBarY.visibility=View.INVISIBLE

                    ikindiZamanHesabi(millisUntilFinished, startTime)
                    aksamZamanHesabi(millisUntilFinished, startTime)
                    yatsiZamanHesabi(millisUntilFinished, startTime)

                    progressBarO.progress=(gecenTime-enDateG.time).toInt()
                    progressBarO.max= milisecondsG.toInt()
                    val currentProgressBar=(gecenTime-enDateG.time).toInt()
//                    progressBarI.progress=currentProgressBar
                    ObjectAnimator.ofInt(progressBarO,"progress",currentProgressBar)
                        .setDuration(1000)
                        .start()


                }
                else if (sonrakiVakitAdi=="Güneş"){

                    progressBarI.visibility=View.INVISIBLE
                    progressBarG.visibility=View.VISIBLE
                    progressBarO.visibility=View.INVISIBLE
                    progressBarIk.visibility=View.INVISIBLE
                    progressBarA.visibility=View.INVISIBLE
                    progressBarY.visibility=View.INVISIBLE

                    ogleZamanHesabi(millisUntilFinished, startTime)
                    ikindiZamanHesabi(millisUntilFinished, startTime)
                    aksamZamanHesabi(millisUntilFinished, startTime)
                    yatsiZamanHesabi(millisUntilFinished, startTime)

                    progressBarG.progress=(gecenTime-enDateI.time).toInt()
                    progressBarG.max= milisecondsI.toInt()
                    val currentProgressBar=(gecenTime-enDateI.time).toInt()
//                    progressBarI.progress=currentProgressBar
                    ObjectAnimator.ofInt(progressBarG,"progress",currentProgressBar)
                        .setDuration(1000)
                        .start()



                }
                else if (sonrakiVakitAdi=="İmsak"){

                    progressBarI.visibility=View.VISIBLE
                    progressBarG.visibility=View.INVISIBLE
                    progressBarO.visibility=View.INVISIBLE
                    progressBarIk.visibility=View.INVISIBLE
                    progressBarA.visibility=View.INVISIBLE
                    progressBarY.visibility=View.INVISIBLE

                    gunesZamanHesabi(millisUntilFinished, startTime)
                    ogleZamanHesabi(millisUntilFinished, startTime)
                    ikindiZamanHesabi(millisUntilFinished, startTime)
                    aksamZamanHesabi(millisUntilFinished, startTime)

                    progressBarI.progress=(gecenTime-enDateY.time).toInt()
                    progressBarI.max= milisecondsY.toInt()
                    val currentProgressBar=(gecenTime-enDateY.time).toInt()
//                    progressBarI.progress=currentProgressBar
                    ObjectAnimator.ofInt(progressBarI,"progress",currentProgressBar)
                        .setDuration(1000)
                        .start()


                }
                else if (sonrakiVakitAdi=="İmsak "){

                    progressBarI.visibility=View.VISIBLE
                    progressBarG.visibility=View.INVISIBLE
                    progressBarO.visibility=View.INVISIBLE
                    progressBarIk.visibility=View.INVISIBLE
                    progressBarA.visibility=View.INVISIBLE
                    progressBarY.visibility=View.INVISIBLE

                    gunesZamanHesabi(millisUntilFinished, startTime)
                    ogleZamanHesabi(millisUntilFinished, startTime)
                    ikindiZamanHesabi(millisUntilFinished, startTime)
                    aksamZamanHesabi(millisUntilFinished, startTime)


                    progressBarI.progress=(gecenTime-enDateY.time).toInt()
//                    var serverUptimeSecondsPr =
//                        (gecenTime-(enDateY.time))
                    progressBarI.max= milisecondsY.toInt()
                    val currentProgressBar=(gecenTime-enDateY.time).toInt()
//                    progressBarI.progress=currentProgressBar
                    ObjectAnimator.ofInt(progressBarI,"progress",currentProgressBar)
                        .setDuration(1000)
                        .start()

//                    Log.e("milisecond",(miliseconds).toString())
//                    Log.e("endate",(enDate.time).toString())
//                    Log.e("currenprogress",currentProgressBar.toString())
//                    Log.e("serveruptime",(serverUptimeSecondsPr).toString())
//                    Log.e("yatsımilis",milisecondsY.toString())
//                    Log.e("yatsiendate",enDateY.time.toString())
//                    Log.e("gecentime",gecenTime.toString())
//                    Log.e("gecentimemili",(gecenTime-enDateY.time).toString())
                }

                if (hoursLeft=="00") {
                    tvAdi.text = minutesLeft + ":" + secondsLeft
                }else{
                    tvAdi.text = hoursLeft + ":" + minutesLeft + ":" + secondsLeft
                }
                if((hoursLeft=="00"&&minutesLeft=="00"&&secondsLeft=="00")||
                        (hoursLeft.toInt()<=0&&minutesLeft.toInt()<=0&&secondsLeft.toInt()<=0)){
                    timer.cancel()
                    veritabaniSorgula()
                    veritabanıVakitListele()
                    vakitKontrol()
                    kalanZamanHesabi(endTime)
                    zamaniBaslat()
                }
            }

            override fun onFinish() {
                veritabaniSorgula()
                veritabanıVakitListele()
                vakitKontrol()
                kalanZamanHesabi(endTime)
                toolbarYukle()
                zamaniBaslat()
//

            }
        }.start()

    }
    private fun kalanZamanHesabi(endTime:String){
        val formatter=SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")
        try {
            enDate=formatter.parse(endTime!!)
            miliseconds=enDate?.time
            enDateRmznKalan=formatter.parse(endTimeRmznKalan)
            milisecondsRmznKalan=enDateRmznKalan.time-miliseconds
            enDateRmznGecen=formatter.parse(endTimeRmznGecen)
            milisecondsRmznGecen=enDateRmznGecen.time-miliseconds
            if (sonrakiVakitAdi=="Yatsı") {
                //progress için önceki vakit ile sonraki vakit farkı hesabı
                enDateA = formatter.parse(endTimeA)
                milisecondsA = miliseconds - enDateA.time
            }
            if (sonrakiVakitAdi=="Akşam"){
                enDateY=formatter.parse(endTimeY)
                milisecondsY=enDateY.time-miliseconds
                //progress için önceki vakit ile sonraki vakit farkı hesabı
                enDateIk=formatter.parse(endTimeIk)
                milisecondsIk=miliseconds-enDateIk.time
            }
            else if (sonrakiVakitAdi=="İkindi"){
                enDateA=formatter.parse(endTimeA)
                milisecondsA=enDateA.time-miliseconds
                enDateY=formatter.parse(endTimeY)
                milisecondsY=enDateY.time-miliseconds
                //progress için önceki vakit ile sonraki vakit farkı hesabı
                enDateO=formatter.parse(endTimeO)
                milisecondsO=miliseconds-enDateO.time
            }
            else if (sonrakiVakitAdi=="Öğle"){
                enDateIk=formatter.parse(endTimeIk)
                milisecondsIk=enDateIk.time-miliseconds
                enDateA=formatter.parse(endTimeA)
                milisecondsA=enDateA.time-miliseconds
                enDateY=formatter.parse(endTimeY)
                milisecondsY=enDateY.time-miliseconds
                //progress için önceki vakit ile sonraki vakit farkı hesabı
                enDateG=formatter.parse(endTimeG)
                milisecondsG=miliseconds-enDateG.time

            }
            else if (sonrakiVakitAdi=="Güneş"){
                enDateO=formatter.parse(endTimeO)
                milisecondsO=enDateO.time-miliseconds
                enDateIk=formatter.parse(endTimeIk)
                milisecondsIk=enDateIk.time-miliseconds
                enDateA=formatter.parse(endTimeA)
                milisecondsA=enDateA.time-miliseconds
                enDateY=formatter.parse(endTimeY)
                milisecondsY=enDateY.time-miliseconds
                //progress için önceki vakit ile sonraki vakit farkı hesabı
                enDateI=formatter.parse(endTimeI)
                milisecondsI=miliseconds-enDateI.time

            }
            else if (sonrakiVakitAdi=="İmsak"){
                enDateG=formatter.parse(endTimeG)
                milisecondsG=enDateG.time-miliseconds
                enDateO=formatter.parse(endTimeO)
                milisecondsO=enDateO.time-miliseconds
                enDateIk=formatter.parse(endTimeIk)
                milisecondsIk=enDateIk.time-miliseconds
                enDateA=formatter.parse(endTimeA)
                milisecondsA=enDateA.time-miliseconds
                //progress için sonraki vakit ile önceki vakit farkı hesabı
                enDateY=formatter.parse(endTimeY)
                milisecondsY=miliseconds-enDateY.time

            }
            else if (sonrakiVakitAdi=="İmsak "){
                enDateG=formatter.parse(endTimeG)
                milisecondsG=enDateG.time-miliseconds
                enDateO=formatter.parse(endTimeO)
                milisecondsO=enDateO.time-miliseconds
                enDateIk=formatter.parse(endTimeIk)
                milisecondsIk=enDateIk.time-miliseconds
                enDateA=formatter.parse(endTimeA)
                milisecondsA=enDateA.time-miliseconds
                //progress için önceki vakit ile sonraki vakit farkı hesabı
                enDateY=formatter.parse(endTimeY)
                milisecondsY=miliseconds-enDateY.time

            }

        }catch (e:Exception){

        }
    }
    @SuppressLint("ResourceAsColor")
    private fun vakitKontrol(){
        endTimeRmznGecen="12.04.2021, 23:59:59"
        endTimeRmznKalan="12.05.2021, 23:59:59"


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
        if (saatBul()==imsak ||(sd[0].toInt()>sdImsak[0].toInt() && sd[0].toInt()<sdGunes[0].toInt()) ||
                ( sd[0].toInt()==sdImsak[0].toInt() && sd[1].toInt()>=sdImsak[1].toInt()) ||  (sd[0].toInt()==sdGunes[0].toInt() && sd[1].toInt()<sdGunes[1].toInt()))

        {
            sonrakiVakit=gunes
            sonrakiVakitAdi="Güneş"
            tvSonrakiVakteKalanSureG.text=sonrakiVakit
            tvAdi=findViewById(R.id.tvSonrakiVakteKalanSureG)

            endTime=tarihBul()+", "+sonrakiVakit+":00"
            endTimeO=tarihBul()+", "+ogle+":00"
            endTimeIk=tarihBul()+", "+ikindi+":00"
            endTimeA=tarihBul()+", "+aksam+":00"
            endTimeY=tarihBul()+", "+yatsi+":00"

            //progressbar için bi önceki vaktin zamanı
            endTimeI=tarihBul()+", "+imsak+":00"

            lY.setBackgroundResource(R.color.gray)
            lI.setBackgroundResource(R.color.yesil)
            lG.setBackgroundColor(Color.RED)
            tvSonrakiVakteKalanSureY.text="00:00"
            tvSonrakiVakteKalanSureI.text="İmsak Vakti"
        }
        else if (saatBul()==gunes ||(sd[0].toInt()>sdGunes[0].toInt() && sd[0].toInt()<sdOgle[0].toInt()) ||
                ( sd[0].toInt()==sdGunes[0].toInt() && sd[1].toInt()>=sdGunes[1].toInt()) ||
                (sd[0].toInt()==sdOgle[0].toInt() && sd[1].toInt()<sdOgle[1].toInt()))
        {
            sonrakiVakit=ogle
            sonrakiVakitAdi="Öğle"

            tvAdi=findViewById(R.id.tvSonrakiVakteKalanSureO)

            endTime=tarihBul()+", "+sonrakiVakit+":00"
            endTimeIk=tarihBul()+", "+ikindi+":00"
            endTimeA=tarihBul()+", "+aksam+":00"
            endTimeY=tarihBul()+", "+yatsi+":00"

            //progressbar için bi önceki vaktin zamanı
            endTimeG=tarihBul()+", "+gunes+":00"

            lI.setBackgroundResource(R.color.gray)
            lG.setBackgroundResource(R.color.yesil)
            lO.setBackgroundColor(Color.RED)
            tvSonrakiVakteKalanSureI.text="00:00"
            tvSonrakiVakteKalanSureG.text="Güneş Vakti"
        }
        else if (saatBul()==ogle ||(sd[0].toInt()>sdOgle[0].toInt() && sd[0].toInt()<sdIkindi[0].toInt()) ||
                ( sd[0].toInt()==sdOgle[0].toInt() && sd[1].toInt()>=sdOgle[1].toInt()) ||
                (sd[0].toInt()==sdIkindi[0].toInt() && sd[1].toInt()<sdIkindi[1].toInt()))
        {
            sonrakiVakit=ikindi
            sonrakiVakitAdi="İkindi"

            tvAdi=findViewById(R.id.tvSonrakiVakteKalanSureIk)

            endTime=tarihBul()+", "+sonrakiVakit+":00"
            endTimeA=tarihBul()+", "+aksam+":00"
            endTimeY=tarihBul()+", "+yatsi+":00"

            //progressbar için bi önceki vaktin zamanı
            endTimeO=tarihBul()+", "+ogle+":00"

            lG.setBackgroundResource(R.color.gray)
            lO.setBackgroundResource(R.color.yesil)
            lIk.setBackgroundColor(Color.RED)
            tvSonrakiVakteKalanSureG.text="00:00"
            tvSonrakiVakteKalanSureO.text="Öğle Vakti"
        }
        else if (saatBul()==ikindi ||(sd[0].toInt()>sdIkindi[0].toInt() && sd[0].toInt()<sdAksam[0].toInt()) ||
                ( sd[0].toInt()==sdIkindi[0].toInt() && sd[1].toInt()>=sdIkindi[1].toInt()) ||
                (sd[0].toInt()==sdAksam[0].toInt() && sd[1].toInt()<sdAksam[1].toInt()))
        {
            sonrakiVakit=aksam
            sonrakiVakitAdi="Akşam"

            tvAdi=findViewById(R.id.tvSonrakiVakteKalanSureA)


            endTime=tarihBul()+", "+sonrakiVakit+":00"
            endTimeY=tarihBul()+", "+yatsi+":00"

            //progressbar için bi önceki vaktin zamanı
            endTimeIk=tarihBul()+", "+ikindi+":00"

            lO.setBackgroundResource(R.color.gray)
            lIk.setBackgroundResource(R.color.yesil)
            lA.setBackgroundColor(Color.RED)
            tvSonrakiVakteKalanSureO.text="00:00"
            tvSonrakiVakteKalanSureIk.text="İkindi Vakti"
        }
        else if (saatBul()==aksam ||(sd[0].toInt()>sdAksam[0].toInt() && sd[0].toInt()<sdYatsi[0].toInt()) ||
                ( sd[0].toInt()==sdAksam[0].toInt() && sd[1].toInt()>=sdAksam[1].toInt())
                ||  (sd[0].toInt()==sdYatsi[0].toInt() && sd[1].toInt()<sdYatsi[1].toInt()))
        {
            sonrakiVakit=yatsi
            sonrakiVakitAdi="Yatsı"

            tvAdi=findViewById(R.id.tvSonrakiVakteKalanSureY)

            endTime=tarihBul()+", "+sonrakiVakit+":00"

            //progressbar için bi önceki vaktin zamanı
            endTimeA=tarihBul()+", "+aksam+":00"

            lIk.setBackgroundResource(R.color.gray)
            lA.setBackgroundResource(R.color.yesil)
            lY.setBackgroundColor(Color.RED)
            tvSonrakiVakteKalanSureIk.text="00:00"
            tvSonrakiVakteKalanSureA.text="Akşam Vakti"
        }
        else if (saatBul()==yatsi ||(sd[0].toInt()>sdYatsi[0].toInt() && sd[0].toInt()<24) ||
                ( sd[0].toInt()==sdYatsi[0].toInt() && sd[1].toInt()>=sdYatsi[1].toInt()))
        {
            sonrakiVakit=yarinkiImsak
            sonrakiVakitAdi="İmsak"

            tvAdi=findViewById(R.id.tvSonrakiVakteKalanSureI)

            endTime=tarihBulYarin()+", "+sonrakiVakit+":00"
            endTimeG=tarihBulYarin()+", "+yarinkiG+":00"
            endTimeO=tarihBulYarin()+", "+yarinkiO+":00"
            endTimeIk=tarihBulYarin()+", "+yarinkiIk+":00"
            endTimeA=tarihBulYarin()+", "+yarinkiA+":00"

            //progressbar için bi önceki vaktin zamanı
            endTimeY=tarihBul()+", "+yatsi+":00"

            lA.setBackgroundResource(R.color.gray)
            lY.setBackgroundResource(R.color.yesil)
            lI.setBackgroundColor(Color.RED)
            tvSonrakiVakteKalanSureA.text="00:00"
            tvSonrakiVakteKalanSureY.text="Yatsı Vakti"


        }
        else if (sd[0].toInt()<sdImsak[0].toInt() || sd[0].toInt()==24 ||
                ( sd[0].toInt()==sdImsak[0].toInt() && sd[1].toInt()<sdImsak[1].toInt()))
        {
            sonrakiVakit=imsak
            sonrakiVakitAdi="İmsak "

            tvAdi=findViewById(R.id.tvSonrakiVakteKalanSureI)
            endTime=tarihBul()+", "+sonrakiVakit+":00"
            endTimeG=tarihBul()+", "+gunes+":00"
            endTimeO=tarihBul()+", "+ogle+":00"
            endTimeIk=tarihBul()+", "+ikindi+":00"
            endTimeA=tarihBul()+", "+aksam+":00"

            //progressbar için bi önceki vaktin zamanı
            endTimeY=tarihBulDun()+", "+yatsi+":00"

            lA.setBackgroundResource(R.color.gray)
            lY.setBackgroundResource(R.color.yesil)
            lI.setBackgroundColor(Color.RED)
            tvSonrakiVakteKalanSureA.text="00:00"
            tvSonrakiVakteKalanSureY.text="Yatsı Vakti"
        }
    }
    private fun tarihBul():String {

        val tarihFormat= SimpleDateFormat("dd.MM.yyyy")
        val tarih= Date()
        val simdiTarih=tarihFormat.format(tarih)
//        Log.e("tarihhh",simdiTarih)


        return simdiTarih.toString()

    }
    private fun gunBul():String{
        val tarihFormat= SimpleDateFormat("dd")
        val tarih= Date()
        val simdiGun=tarihFormat.format(tarih)
//        Log.e("tarihhh",simdiTarih)


        return simdiGun.toString()
    }
    private fun saatBul():String{
        val saatFormat= SimpleDateFormat("kk:mm")
        val saat= Date()
        val simdiSaat=saatFormat.format(saat).toString()
        //       Log.e("saat",simdiSaat)
        return simdiSaat.toString()
    }
    private fun tarihBulDun():String {

        val tarihFormat= SimpleDateFormat("dd.MM.yyyy")
        val tarih= Date()
        val tarih2=Date(tarih.time-(24 * 60 * 60 * 1000))
        val dunkiTarih=tarihFormat.format(tarih2)
//        Log.e("yarınki tarih",yarinkiTarih)


        return dunkiTarih.toString()

    }
    private fun tarihBulYarin():String {

        val tarihFormat= SimpleDateFormat("dd.MM.yyyy")
        val tarih= Date()
        val tarih2=Date(tarih.time+(24 * 60 * 60 * 1000))
        val yarinkiTarih=tarihFormat.format(tarih2)
//        Log.e("yarınki tarih",yarinkiTarih)


        return yarinkiTarih.toString()

    }
    private fun veritabanıYarinkiImsakListele() {
        var sonuc=false
        veritabaniSorgula()
        val vt = DatabaseVakitler(this)
        val vakitlerListe = VakitlerDao().vakitListele(vt)
        for (k in vakitlerListe) {
            if (tarihBulYarin()==k.miladiTarih){

                yarinkiImsak=k.imsak
                yarinkiG=k.gunes
                yarinkiO=k.ogle
                yarinkiIk=k.ikindi
                yarinkiA=k.aksam
                yarinkiY=k.yatsi
                sonuc=true
//              Log.e("yarınki imsak",yarinkiImsak)
                break

            }

        }
        if (sonuc==false){
            listele()
        }

    }
    fun vakitSorgula(){
        val vt = DatabaseVakitler(this)
        val vakitlerListe = VakitlerDao().vakitListele(vt)
        if (vakitlerListe.size<=15 || vakitlerListe.isEmpty()){
            listele()
        }
    }
    private fun veritabanıVakitListele() {
        var sonuc=false
        veritabaniSorgula()
        val tarih=tarihBul()
        val vt = DatabaseVakitler(this)
        val vakitlerListe = VakitlerDao().vakitListele(vt)
        if (vakitlerListe.size<=15|| vakitlerListe.isEmpty()){
            listele()
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
                hTarih=k.hicriTarih
                ayUrl=k.ayUrl
                sonuc=true
//                Log.e("imsak service",imsak)
                break

            }

        }
        if (sonuc==false){
            listele()
        }

    }
    private fun veritabaniSorgula(){
        val vt= DatabaseKonum(this)
        val konumlarListe= Konumlardao().konumListele(vt)
        for (k in konumlarListe){
            konumId=k.ilceId
            ilceAdi=k.ilce
            sehirAdi=k.sehir
//            Log.e("ilçe id",konumId)
        }

    }
    private fun listele(){


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
                val responseBody = response.body()!!

                val vt = DatabaseVakitler(applicationContext)
                val vakitlerListe = VakitlerDao().vakitListele(vt)
//                Log.e("vakitlerListe",vakitlerListe.toString())
                if (!vakitlerListe.isEmpty()||vakitlerListe.size!=0) {
                    for (k in vakitlerListe) {
                        VakitlerDao().vakitSil(vt, k.id)
//                        Log.e("imsak veritabanı L", k.imsak)
                    }
                }



                for (r in responseBody) {

                    VakitlerDao().vakitEkle(vt, "${r.Imsak}", "${r.Gunes}",
                            "${r.Ogle}", "${r.Ikindi}", "${r.Aksam}", "${r.Yatsi}",
                            "${r.MiladiTarihKisa}", "${r.HicriTarihKisa}", "${r.AyinSekliURL}")


                }
                //               Log.e("namazListe",imsak+gunes)


            }

            override fun onFailure(call: Call<List<NamazJsonItem>>, t: Throwable) {
                //               Log.e("MainActivity",t.message.toString())
            }
        })
    }

    override fun onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            val yeniIntent= Intent(Intent.ACTION_MAIN)
            yeniIntent.addCategory(Intent.CATEGORY_HOME)
            yeniIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(yeniIntent)
        }

    }
    private fun yatsiZamanHesabi(millisUntilFinished:Long,startTime:Long){
        var  serverUptimeSecondsY = (millisUntilFinished+milisecondsY - startTime) / 1000
        var hoursLeftY = String.format("%02d", (serverUptimeSecondsY % 86400) / 3600)
        var minutesLeftY = String.format("%02d", ((serverUptimeSecondsY % 86400) % 3600) / 60)
        var secondsLeftY = String.format("%02d", ((serverUptimeSecondsY % 86400) % 3600) % 60)
        tvSonrakiVakteKalanSureY.text=hoursLeftY + ":" + minutesLeftY + ":" + secondsLeftY
    }
    private fun aksamZamanHesabi(millisUntilFinished:Long,startTime:Long){
        var  serverUptimeSecondsA = (millisUntilFinished+milisecondsA - startTime) / 1000

        var hoursLeftA = String.format("%02d", (serverUptimeSecondsA % 86400) / 3600)
        var minutesLeftA = String.format("%02d", ((serverUptimeSecondsA % 86400) % 3600) / 60)
        var secondsLeftA = String.format("%02d", ((serverUptimeSecondsA % 86400) % 3600) % 60)
        tvSonrakiVakteKalanSureA.text=hoursLeftA + ":" + minutesLeftA + ":" + secondsLeftA

    }
    private fun ikindiZamanHesabi(millisUntilFinished:Long,startTime:Long){
        var  serverUptimeSecondsIk = (millisUntilFinished+milisecondsIk - startTime) / 1000

        var hoursLeftIk = String.format("%02d", (serverUptimeSecondsIk % 86400) / 3600)
        var minutesLeftIk = String.format("%02d", ((serverUptimeSecondsIk % 86400) % 3600) / 60)
        var secondsLeftIk = String.format("%02d", ((serverUptimeSecondsIk % 86400) % 3600) % 60)
        tvSonrakiVakteKalanSureIk.text=hoursLeftIk + ":" + minutesLeftIk + ":" + secondsLeftIk

    }
    private fun ogleZamanHesabi(millisUntilFinished:Long,startTime:Long){
        var  serverUptimeSecondsO = (millisUntilFinished+milisecondsO - startTime) / 1000

        var hoursLeftO = String.format("%02d", (serverUptimeSecondsO % 86400) / 3600)
        var minutesLeftO = String.format("%02d", ((serverUptimeSecondsO % 86400) % 3600) / 60)
        var secondsLeftO = String.format("%02d", ((serverUptimeSecondsO % 86400) % 3600) % 60)
        tvSonrakiVakteKalanSureO.text=hoursLeftO + ":" + minutesLeftO + ":" + secondsLeftO

    }
    private fun gunesZamanHesabi(millisUntilFinished:Long,startTime:Long){
        var  serverUptimeSecondsG = (millisUntilFinished+milisecondsG - startTime) / 1000

        var hoursLeftG = String.format("%02d", (serverUptimeSecondsG % 86400) / 3600)
        var minutesLeftG = String.format("%02d", ((serverUptimeSecondsG % 86400) % 3600) / 60)
        var secondsLeftG = String.format("%02d", ((serverUptimeSecondsG % 86400) % 3600) % 60)
        tvSonrakiVakteKalanSureG.text=hoursLeftG + ":" + minutesLeftG + ":" + secondsLeftG

    }
}