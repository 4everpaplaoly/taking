package com.example.taking

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.widget.TextView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.taking.databinding.ActivityWeeklyReportBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WeeklyReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeeklyReportBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeeklyReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        setupBottomNav()
        loadWeeklyReport()
    }

    /** 🔥 하단 네비게이션 */
    private fun setupBottomNav() {

        binding.bottomNavigationViewWeekly.selectedItemId = R.id.nav_report

        binding.bottomNavigationViewWeekly.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }

                R.id.nav_report -> true

                R.id.nav_setting -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }
    }

    /** 🔥 코루틴으로 주간 리포트 불러오기 */
    private fun loadWeeklyReport() {

        CoroutineScope(Dispatchers.IO).launch {

            // 로그인 사용자 이메일 가져오기
            val pref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val userEmail = pref.getString("user_email", null)

            if (userEmail.isNullOrEmpty()) {
                runOnUiThread {
                    binding.txtSummarySub.text = "로그인된 사용자 정보가 없습니다."
                }
                return@launch
            }

            // 날짜 계산
            val today = Calendar.getInstance()
            val endDate = today.time
            today.add(Calendar.DAY_OF_YEAR, -6)
            val startDate = today.time

            val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
            val dateRange = "${sdf.format(startDate)} ~ ${sdf.format(endDate)}"

            runOnUiThread {
                binding.txtWeeklyRange.text = dateRange
            }

            // DB: 지난 7일 기록 가져오기
            val weeklyList = db.medicineDao().getByDateRangeForUser(
                startDate.time,
                endDate.time,
                userEmail
            )

            val totalCount = weeklyList.size
            val avgCount = if (totalCount > 0) totalCount / 7 else 0

            runOnUiThread {
                binding.txtSummarySub.text = "총 ${totalCount}개 복용, 하루 평균 ${avgCount}개"
            }

            // 🔥 리스트뷰 대신 LinearLayout에 직접 아이템 추가
            addWeeklyItems(weeklyList)

            // 🔥 Fake AI 리포트 생성
            generateFakeAiReport(weeklyList.size)
        }
    }

    /** 🔥 리스트 항목 직접 생성 → ScrollView 안에서도 작동 */
    private fun addWeeklyItems(list: List<MedicineEntity>) {

        runOnUiThread {
            binding.weeklyListContainer.removeAllViews()

            for (item in list) {
                val tv = TextView(this)
                tv.text = "약 이름: ${item.name}\n증상: ${item.symptom}"
                tv.textSize = 16f
                tv.setTextColor(Color.BLACK)
                tv.setPadding(30, 30, 30, 30)
                tv.setBackgroundColor(Color.parseColor("#FAFAFA"))

                // margin 적용
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                tv.layoutParams = params

                binding.weeklyListContainer.addView(tv)
            }
        }
    }

    /** 🔥 Fake ChatGPT 리포트 */
    private fun generateFakeAiReport(count: Int) {

        val report = if (count == 0) {
            """
            지난주에는 기록된 복용 내역이 없어요 😊
            이번 주는 증상이 있을 때 꼭 기록해 주세요!
            """.trimIndent()
        } else {
            """
            📌 **AI 주간 건강 리포트**

            지난 7일 동안 총 ${count}개의 약을 복용했어요.

            ✔️ **좋았던 점**
            - 꾸준히 약을 복용하며 증상을 관리하려는 모습이 좋아요.
            - 기록을 남겨서 패턴을 확인할 수 있어 건강 관리에 큰 도움이 돼요.

            ⚠️ **주의할 점**
            - 증상이 반복되는 약이 있다면 병원 상담이 필요할 수 있어요.
            - 약 복용 간의 시간도 함께 확인하면 더 정확한 관리가 가능해요.

            ⭐ **다음 주 추천 한 줄**
            “복용 시간 + 증상을 함께 기록하면 훨씬 정확한 관리가 가능해요!”
            """.trimIndent()
        }

        runOnUiThread {
            binding.txtAiSummary.text = report
        }
    }
}
