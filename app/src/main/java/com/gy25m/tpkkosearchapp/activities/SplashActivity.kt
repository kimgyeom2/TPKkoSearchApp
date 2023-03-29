package com.gy25m.tpkkosearchapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.gy25m.tpkkosearchapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash) //테마를 이용하여 화면구성

        // 1.5초 후에 로그인 화면으로 이동
//        Handler(Looper.getMainLooper()).postDelayed(object : Runnable{
//            override fun run() {
//                TODO("Not yet implemented")
//            }
//        },1500)    람다로 축약
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        },1500)
    }
}