package com.gy25m.tpkkosearchapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.gy25m.tpkkosearchapp.R
import com.gy25m.tpkkosearchapp.databinding.ActivityMainBinding
import com.gy25m.tpkkosearchapp.fragments.PlaceListFragment
import com.gy25m.tpkkosearchapp.fragments.PlaceMapFragment
import com.gy25m.tpkkosearchapp.model.KakaoSearchPlaceResponse
import com.gy25m.tpkkosearchapp.network.RetrofitApiService
import com.gy25m.tpkkosearchapp.network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    // 카카오 검색에 필요한 요청 데이터 : query(검색장소명) , x(경도:longitude), y(위도:latitude)
    // 1. 검색 장소명
    var searchQuery:String="화장실"  // 앱의 초기 검색어 - 내 주변의 개방된 화장실
    // 2. 현재 내위치 정보 객체(위도,경도 정보를 멤버로 보유한객체)
    var myLocation:Location?=null

    // [ Google Fused Location API 사용 : play-services-location ]
    val providerClient:FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    // 검색결과 응답 객체 참조변수
    var searchPlaceResponse:KakaoSearchPlaceResponse?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //작업
        //툴바를 제목줄로 대체 - 옵션메뉴랑 연결되도록
        setSupportActionBar(binding.toolbar)

        // 처음에 보여질 fragment를 동적추가
        supportFragmentManager.beginTransaction().add(R.id.container_fragment,PlaceListFragment()).commit()
        // 탭레이아웃 탭버튼 클릭시 보여줄 프래그먼트 변경
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.text=="List"){
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment,PlaceListFragment()).commit()
                }else if (tab?.text=="Map"){
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment,PlaceMapFragment()).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        // 소프트키보드의 검색버튼 클릭하였을떄..
        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            searchQuery=binding.etSearch.text.toString()
            // 카카오 검색 API를 이용하여 장소들 검색하기
            searchPlace()
            
            false
        }

        // 특정 키워드로 된 단축 검색 button들에 listener 처리하는 함수 호출
        choiceButtonsListener()

        // 내 위치 정보 제공에대한 동적 퍼미션 요청
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED){
            // 퍼미션 요청 대행사 이용-계약 체결
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            // 내 위치 요청
            requestMyLocation()
        }
        binding.ivMyLocation.setOnClickListener { requestMyLocation() }

    }//onCreate method..

    // 퍼미션 요청 대행사 계약 및 등록
    val permissionLauncher:ActivityResultLauncher<String> =registerForActivityResult(ActivityResultContracts.RequestPermission(),object : ActivityResultCallback<Boolean>{
        override fun onActivityResult(result: Boolean?) {
            if (result!!) requestMyLocation()
            else Toast.makeText(this@MainActivity, "위치정보 제공에 동의하지 않았습니다. 검색기능이 제한됩니다", Toast.LENGTH_SHORT).show()
        }
    })

    // 내위치 요청 작업 메소드
    private fun requestMyLocation(){
        // 위치검색 기준 설정하는 요청객체
        val request:LocationRequest=LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()

        // 실시간 위치정보를 갱신하도록 요청
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }   // if문 error클릭 자동완성
        providerClient.requestLocationUpdates(request,locationCallback, Looper.getMainLooper())
    }

    // 위치 검색결과 callback 객체
    private val locationCallback:LocationCallback=object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            myLocation=p0.lastLocation

            //위치 탐색되었으니 실시간 업데이트를 종료
            providerClient.removeLocationUpdates(this) //this : locationCallback 객체

            // 위치정보 얻었으니 검색을 시작
            searchPlace()
        }
    }

    // 카카오 장소검색 API를 파싱하는 작업메소드
    private fun searchPlace(){
        // Toast.makeText(this, "$searchQuery - ${myLocation?.latitude} , ${myLocation?.longitude}", Toast.LENGTH_SHORT).show()
        // Kakao keyword place search api.. Rest API작업 - Retrofit
        val retrofit:Retrofit=RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitApiservice=retrofit.create(RetrofitApiService::class.java)
        retrofitApiservice.searchplace(searchQuery,myLocation?.latitude.toString(),myLocation?.longitude.toString()).enqueue(object : Callback<KakaoSearchPlaceResponse>{
            override fun onResponse(
                call: Call<KakaoSearchPlaceResponse>,
                response: Response<KakaoSearchPlaceResponse>
            ) {
                searchPlaceResponse=response.body()
                //Toast.makeText(this@MainActivity, "${searchPlaceResponse?.meta?.total_count}", Toast.LENGTH_SHORT).show()

                // 검색이 완료가 되면 무조건 ListFragment 부터 보여주기
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment,PlaceListFragment()).commit()
                // 탭버튼의 위치를 listFragment로 변경
                binding.tabLayout.getTabAt(0)?.select()
            }

            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버문제가 있습니다", Toast.LENGTH_SHORT).show()
            }
        })
        
    }

    // 특정키워드 검색 단축 버튼들리스너 처리
    private fun choiceButtonsListener(){
        binding.layoutChoice.choiceWc.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceMovie.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceGas.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceEv.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePharmacy.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePark.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFood.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceCoffee.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice1.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice2.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice3.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice4.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice5.setOnClickListener { clickChoice(it) }
    }

    // 멤버변수 영역
    var choiceId = R.id.choice_wc
    private fun clickChoice(view:View){
        // 기존 선택되었던 버튼을 찾아서 배경이미지를 bg_choice로 변경
        findViewById<ImageView>(choiceId).setBackgroundResource(R.drawable.bg_choice)

        // 현재 클릭된 버튼의 배경을 bg_choice_select로 변경
        view.setBackgroundResource(R.drawable.bg_choice_select)

        // 다음 클릭시에 이전클릭된 뷰의 ID를 기억하도록
        choiceId=view.id

        // 초이스한 것에 따라 검색장소명을 변경하여 다시 검색
        when(choiceId){
            R.id.choice_wc -> searchQuery="화장실"
            R.id.choice_movie -> searchQuery="영화관"
            R.id.choice_gas -> searchQuery="주유소"
            R.id.choice_ev -> searchQuery="전기차충전소"
            R.id.choice_pharmacy -> searchQuery="약국"
            R.id.choice_park -> searchQuery="공원"
            R.id.choice_food -> searchQuery="맛집"
            R.id.choice_coffee -> searchQuery="카페"
            R.id.choice1 -> searchQuery="도서관"
            R.id.choice2 -> searchQuery="헬스장"
            R.id.choice3 -> searchQuery="버스정류장"
            R.id.choice4 -> searchQuery="노래방"
            R.id.choice5 -> searchQuery="사우나"
        }
        // 새로운 검색 시작
        searchPlace()

        // 검색창에 글씨가 있다면 지우기
        binding.etSearch.text.clear()
        binding.etSearch.clearFocus()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_aa-> Toast.makeText(this, "검색장소를 눌러주세요", Toast.LENGTH_SHORT).show()
            R.id.menu_bb-> Toast.makeText(this, "Retrofit, Glide, Kakao API,Naver API,Google API,Gson", Toast.LENGTH_SHORT).show()
        }

        return super.onOptionsItemSelected(item)
    }

}