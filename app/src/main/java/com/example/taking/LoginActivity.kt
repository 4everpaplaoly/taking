package com.example.taking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taking.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {

            val email = binding.editEmail.text.toString().trim()
            val pw = binding.editPassword.text.toString().trim()

            if (email.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedEmail = pref.getString("user_email", null)
            val savedPwd = pref.getString("user_pwd", null)

            if (email == savedEmail && pw == savedPwd) {
                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()

                // 메인 화면으로 이동
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {
                Toast.makeText(this, "이메일 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 회원가입 화면 이동
        binding.txtGoSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
