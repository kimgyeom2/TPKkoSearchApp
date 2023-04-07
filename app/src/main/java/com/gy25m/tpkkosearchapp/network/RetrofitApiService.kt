package com.gy25m.tpkkosearchapp.network

import com.google.cloud.audit.AuthorizationInfo
import com.gy25m.tpkkosearchapp.model.KakaoSearchPlaceResponse
import com.gy25m.tpkkosearchapp.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitApiService {

    // 네아로 사용자정보 API
    @GET("/v1/nid/me")
    fun getNidUserInfo(@Header("Authorization") authorization:String) : Call<NidUserInfoResponse>

    // 카카오의 키워드 장소 검색 API
    @Headers("Authorization: KakaoAK 43fc067003f239ccb609c880dafc4547")
    @GET("/v2/local/search/keyword.json")
    fun searchplace(@Query("query") query:String,@Query("y")latitude:String,@Query("x")longitude:String):Call<KakaoSearchPlaceResponse>


}