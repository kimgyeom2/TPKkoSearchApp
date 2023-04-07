package com.gy25m.tpkkosearchapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gy25m.tpkkosearchapp.R
import com.gy25m.tpkkosearchapp.databinding.ActivityPlaceUrlBinding

class PlaceUrlActivity : AppCompatActivity() {
    val binding by lazy { ActivityPlaceUrlBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.wv.webViewClient=android.webkit.WebViewClient()  // 현재 웹뷰안에서 웹문서 열리도록
        binding.wv.webChromeClient=android.webkit.WebChromeClient() // 웹문서안에서 다이얼로그 같은것을 발동하도록

        binding.wv.settings.javaScriptEnabled=true // 웹뷰는 기본적으로 보안문제로인해 JS동작을 막아놓음

        var place_url:String=intent.getStringExtra("place_url") ?: ""
        binding.wv.loadUrl(place_url)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if (binding.wv.canGoBack()) binding.wv.goBack()
        else super.onBackPressed()
    }
}