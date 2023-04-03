package com.gy25m.tpkkosearchapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.gy25m.tpkkosearchapp.G
import com.gy25m.tpkkosearchapp.R
import com.gy25m.tpkkosearchapp.databinding.ActivityEmailSigninBinding
import com.gy25m.tpkkosearchapp.model.UserAccount

class EmailSigninActivity : AppCompatActivity() {
    val binding by lazy { ActivityEmailSigninBinding.inflate(layoutInflater)  }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)
        // 툴바에 업버튼 보이기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnSignin.setOnClickListener{clickSignIn()}
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
    private fun clickSignIn(){
        var email=binding.etEmail.text.toString()
        var password=binding.etPassword.text.toString()

        //Firebase Firestore DB에서 이메일,비밀번호 확인
        val db=FirebaseFirestore.getInstance()
        db.collection("emailUsers")
            .whereEqualTo("email",email)   //해당 이메일있는지 비교
            .whereEqualTo("password",password) //해당 비밀번호있는지 비교
            .get().addOnSuccessListener {
                if (it.documents.size>0){
                    //로그인 성공
                    var id:String=it.documents[0].id  //회원번호
                    G.userInfo= UserAccount(id,email)

                    // 로그인 성공했으니 곧바로 Main이동
                    val intent=Intent(this,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    // 기존 task의 모든 액티비티 제거하고 새로운 task시작
                    // -> 앞에 로그인 액티비티 회원가입 액티비티 다 닫고 넘어가기
                }else{
                    // 로그인 실패
                    AlertDialog.Builder(this).setMessage("일치하는 회원정보가 없습니다").show()
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()
                }
            }

    }
}