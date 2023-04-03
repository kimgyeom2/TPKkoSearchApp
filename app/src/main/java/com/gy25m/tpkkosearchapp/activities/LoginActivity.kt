package com.gy25m.tpkkosearchapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.gy25m.tpkkosearchapp.G
import com.gy25m.tpkkosearchapp.R
import com.gy25m.tpkkosearchapp.databinding.ActivityLoginBinding
import com.gy25m.tpkkosearchapp.model.NidUserInfoResponse
import com.gy25m.tpkkosearchapp.model.UserAccount
import com.gy25m.tpkkosearchapp.network.RetrofitApiService
import com.gy25m.tpkkosearchapp.network.RetrofitHelper
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 둘러보기 버튼 클릭으로 로그인없이 바로 메인으로 이동하기
        binding.tvGo.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        // 회원가입버튼 클릭
        binding.tvSignup.setOnClickListener{
            // 회원가입 화면 전환
            startActivity(Intent(this,SignupActivity::class.java))
        }

        // 이메일 로그인 버튼 클릭
        binding.layoutEmail.setOnClickListener{
            // 이메일로그인 화면 전환
            startActivity(Intent(this,EmailSigninActivity::class.java))
        }

        // 간편로그인 버튼들 클릭
        binding.ivLoginKakao.setOnClickListener { clickLoginKakao() }
        binding.ivLoginGoogle.setOnClickListener { clickLoginGoogle() }
        binding.ivLoginNaver.setOnClickListener { clickLoginNaver() }

        //카카오 키해시값 얻어오기
        Log.i("keyHash",Utility.getKeyHash(this).toString())
    }//oncreate
    private fun clickLoginKakao(){

        // 카카오 로그인의 공통 콜백 함수
        val callback:(OAuthToken?,Throwable?)->Unit={token,error->
            if (token!=null){
                Toast.makeText(this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()

                // 사용자 정보 요청 [1.회원주소 2.이메일주소]
                UserApiClient.instance.me { user, error ->
                    if (user!=null){
                        var id:String=user.id.toString()
                        var email:String= user.kakaoAccount?.email ?: ""  //혹시 null이면 이메일의 기본값은 ""
                        Toast.makeText(this, "$email", Toast.LENGTH_SHORT).show()
                        G.userInfo= UserAccount(id,email)

                        // main화면으로 이동
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                }
            }else{
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        // 카카오톡이 설치되어 있다면 카톡으로 로그인, 아니면 카카오계정 로그인
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }
    private fun clickLoginGoogle(){
        // Google에서 검색 [안드로이드 구글 로그인]

        // 구글 로그인 옵션객체
        val signInOptions:GoogleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        // 구글 로그인 화면(액티비티)를 실행하는 인텐트를 통해 로그인 구현
        val intent:Intent=GoogleSignIn.getClient(this,signInOptions).signInIntent
        resultLauncher.launch(intent)
    }

    // 구글 로그인 화면(액티비티)실행결과를 받아오는 계약 체결 대행사
    val resultLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object:ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {
            // 로그인 결과를 가져온 intent객체 소환
            val intent:Intent?=result?.data

            // 돌아온 Intent로 부터 구글계정 정보를 가져오는 작업수행
            val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)

            val account:GoogleSignInAccount=task.result
            var id:String=account.id.toString()
            var email:String=account.email ?: ""

            Toast.makeText(this@LoginActivity, "$email", Toast.LENGTH_SHORT).show()
            G.userInfo=UserAccount(id,email)

            //main으로 이동
            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
            finish()
        }
    })

    private fun clickLoginNaver(){
        //네이버 아이디로 초기화
        NaverIdLoginSDK.initialize(this,"za5X8b9JhYqLdO91Olyu","T4HVMoVz3F","찾아줘 장소")

        //네이버 로그인
        NaverIdLoginSDK.authenticate(this,object :OAuthLoginCallback{
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(this@LoginActivity, "error : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@LoginActivity, "로그인 실패 : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                // 사용자 정보를 가져오는 REST API를 작업할때 접속토큰 필요함
                val accessToken:String? = NaverIdLoginSDK.getAccessToken()
                // 토큰값 확인
                Log.i("token",accessToken!!)

                // 레트로핏으로 사용자 정보API 가져오기
                val retrofit=RetrofitHelper.getRetrofitInstance("https://openapi.naver.com")
                retrofit.create(RetrofitApiService::class.java).getNidUserInfo("Bearer $accessToken").enqueue(object :Callback<NidUserInfoResponse>{
                    override fun onResponse(
                        call: Call<NidUserInfoResponse>,
                        response: Response<NidUserInfoResponse>
                    ) {
                        val userInfoResponse:NidUserInfoResponse?=response.body()
                        val id:String=userInfoResponse?.response?.id ?: ""
                        val email:String=userInfoResponse?.response?.email ?: ""
                        Toast.makeText(this@LoginActivity, "$email", Toast.LENGTH_SHORT).show()
                        G.userInfo= UserAccount(id,email)
                        
                        //main으로 이동
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                        finish()
                    }

                    override fun onFailure(call: Call<NidUserInfoResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "회원정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        })
    }
}