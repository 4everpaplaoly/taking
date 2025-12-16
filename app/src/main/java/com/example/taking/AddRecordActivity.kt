package com.example.taking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taking.databinding.ActivityAddRecordBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            saveRecord()
        }
    }

    private fun saveRecord() {
        val name = binding.editMedicineName.text.toString().trim()
        val symptom = binding.editSymptom.text.toString().trim()

        if (name.isEmpty() || symptom.isEmpty()) {
            Toast.makeText(this, "약 이름과 증상을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ 현재 로그인한 사용자 이메일 불러오기
        val pref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val userEmail = pref.getString("user_email", null)

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val entity = MedicineEntity(
            name = name,
            symptom = symptom,
            timestamp = System.currentTimeMillis(),
            userEmail = userEmail     // ✅ 여기!
        )

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getInstance(this@AddRecordActivity)
                .medicineDao()
                .insert(entity)

            runOnUiThread {
                Toast.makeText(this@AddRecordActivity, "기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
