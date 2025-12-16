package com.example.taking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taking.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // 회원가입 버튼 클릭
        binding.btnRegister.setOnClickListener {

            val name = binding.editRegisterName.text.toString()
            val email = binding.editRegisterEmail.text.toString()
            val pwd = binding.editRegisterPwd.text.toString()
            val pwdCheck = binding.editRegisterPwdCheck.text.toString()

            // 필수 입력 체크
            if (name.isBlank() || email.isBlank() || pwd.isBlank() || pwdCheck.isBlank()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 비밀번호 확인
            if (pwd != pwdCheck) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 중복 계정 체크
            val savedEmail = pref.getString("user_email", null)
            if (savedEmail == email) {
                Toast.makeText(this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // SharedPreferences 저장
            pref.edit().apply {
                putString("user_name", name)
                putString("user_email", email)
                putString("user_pwd", pwd)
            }.apply()

            Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show()

            // 로그인 화면으로 이동
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // 이미 계정 있어요? → 로그인 이동
        binding.txtGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
