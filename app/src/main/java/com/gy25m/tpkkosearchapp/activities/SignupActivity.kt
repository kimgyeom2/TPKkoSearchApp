package com.gy25m.tpkkosearchapp.activities

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.gy25m.tpkkosearchapp.R
import com.gy25m.tpkkosearchapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var binding:ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)
        // 액션바의 업버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnSignup.setOnClickListener{clickSignUp()}
    }

    override fun onSupportNavigateUp(): Boolean {  //(액션바) 리스너
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickSignUp(){
        // Firebase Firestore DB에 사용자 정보 저장
        var email:String=binding.etEmail.text.toString()
        var password:String=binding.etPassword.text.toString()
        var passwordConfirm:String=binding.etPasswordConfirm.text.toString()

        // 유효성 검사 - 패스워드와 패스워드 확인이 맞는지만 검사
        // 자바에서는 String 비교 ==과 equals차이가 있었는데 코틀린은 상관 x
        if (password!=passwordConfirm){
            AlertDialog.Builder(this).setMessage("패스워드 확인에 문제가 있습니다\n다시 입력해주세요").create().show()
            binding.etPasswordConfirm.selectAll() // 커서를 잡아줌
            return
        }

        //Firestore DB에 저장
        val db=FirebaseFirestore.getInstance()  //연동완료 네트워크 권한,스트림 다안해도됨

        // 저장할 값(email,password) hashmap으로 저장
        val user:MutableMap<String,String> = mutableMapOf()  //mutableMap 생성
        user.put("email",email)
        user["password"]=password  // 이렇게 써도됨

        // 컬렉션 명은 "emailUsers"  [RDBMS의 테이블명같은거]  firebase구조 컬렉션/로우-행/컬럼-열
        // 중복된 email을 가진 회원정보가 없는지 확인
        db.collection("emailUsers")
            .whereEqualTo("email",email)
            .get().addOnSuccessListener {
                // 같은 값을가진 document가 있다면.. 사이즈가 0개 이상일 것이므로
                if(it.documents.size>0){

                    AlertDialog.Builder(this).setMessage("중복된 이메일이 있습니다 다시확인하시길 바랍니다").show() //creat생략가능
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()

                }else{

                    // 랜덤하게 만들어지는 document명을 회원 id값으로 사용할 예정/ v저장
                    db.collection("emailUsers").document().set(user).addOnSuccessListener {
                        AlertDialog.Builder(this)
                            .setMessage("축하합니다\n회원가입이 완료되었습니다")
                            .setPositiveButton("확인",object:OnClickListener{
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    finish()
                                }
                            }).create().show()
                    }
                }
            }

        //document에 안쓰면 랜덤(회원번호)
        // .document().set(user) -> .add(user)로 써도됨 자동랜덤

    }

}