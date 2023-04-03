package com.gy25m.tpkkosearchapp.network

import com.google.cloud.audit.AuthorizationInfo
import com.gy25m.tpkkosearchapp.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface RetrofitApiService {

    // 네아로 사용자정보 API
    @GET("/v1/nid/me")
    fun getNidUserInfo(@Header("Authorization") authorization:String) : Call<NidUserInfoResponse>
}