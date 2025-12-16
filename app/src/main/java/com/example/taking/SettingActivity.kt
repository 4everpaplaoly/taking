package com.example.taking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taking.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = getSharedPreferences("user_pref", MODE_PRIVATE)

        val name = pref.getString("user_name", null)
        val email = pref.getString("user_email", null)

        // -------------------------
        // 🔥 로그인 여부 UI 표시
        // -------------------------
        if (email.isNullOrEmpty()) {
            binding.txtUserName.text = "로그인이 필요합니다"
            binding.txtUserEmail.text = ""

            binding.btnLoginOrLogout.text = "로그인하기"
            binding.btnLoginOrLogout.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

        } else {
            binding.txtUserName.text = name ?: "사용자"
            binding.txtUserEmail.text = email

            binding.btnLoginOrLogout.text = "로그아웃하기"
            binding.btnLoginOrLogout.setOnClickListener {

                pref.edit().clear().apply()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        // -------------------------
        // 🔥 하단 네비게이션 초기 선택 상태 설정
        // -------------------------
        binding.bottomNavigationView.selectedItemId = R.id.nav_setting

        // -------------------------
        // 🔥 하단 네비게이션 이동 기능
        // -------------------------
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }

                R.id.nav_report -> {
                    startActivity(Intent(this, WeeklyReportActivity::class.java))
                    finish()
                    true
                }

                R.id.nav_setting -> true   // 현재 화면
                else -> false
            }
        }
    }
}
