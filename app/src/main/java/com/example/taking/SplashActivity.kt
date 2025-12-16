package com.example.taking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val pref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val email = pref.getString("user_email", null)

            if (email.isNullOrEmpty()) {
                // 로그인 기록 없음 → 로그인 화면으로 이동
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // 자동 로그인 → 메인 화면으로 이동
                startActivity(Intent(this, MainActivity::class.java))
            }

            finish()

        }, 3000) // 3초 스플래시

    }
}
