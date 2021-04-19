package com.erdemselvi.namazvakitleri

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

class SplashActivity : AppCompatActivity() {

    private lateinit var timer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        timer = object : CountDownTimer(3000, 1000) {

            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                // val intent=Intent(this,KonumSecActivity::class.java)
                val intent = Intent(applicationContext, KonumSecActivity::class.java)
                startActivity(intent)
                finish()

            }
        }.start()
    }
}