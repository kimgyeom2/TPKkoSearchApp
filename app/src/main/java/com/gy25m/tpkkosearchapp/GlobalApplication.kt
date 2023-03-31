package com.gy25m.tpkkosearchapp

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //kakao SDK 초기화 - 개발자 사이트에 등록한 [네이티브 앱키]
        KakaoSdk.init(this,"671635e5ac4e4fbb55dd565f8b9c95fc")
    }
}