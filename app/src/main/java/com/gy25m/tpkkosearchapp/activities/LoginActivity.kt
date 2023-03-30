package com.gy25m.tpkkosearchapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gy25m.tpkkosearchapp.R
import com.gy25m.tpkkosearchapp.databinding.ActivityLoginBinding

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

    }//oncreate
    private fun clickLoginKakao(){
        //firebase이용 예정

    }
    private fun clickLoginGoogle(){

    }
    private fun clickLoginNaver(){

    }
}