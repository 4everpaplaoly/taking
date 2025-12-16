package com.example.taking

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.taking.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dayList: List<TextView>
    private var todayIndex = 0   // 월=0 ~ 일=6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 요일 리스트 (월~일)
        dayList = listOf(
            binding.dayMon,
            binding.dayTue,
            binding.dayWed,
            binding.dayThu,
            binding.dayFri,
            binding.daySat,
            binding.daySun
        )

        // 초기 UI 설정
        setTodayHeader()
        detectTodayIndex()
        moveHighlightToToday()
        colorTodayTextOnly()

        // 테스트용 더미 데이터 삽입 (필요하면 사용)
        insertDummyRecordsOnce()

        // 기록 추가 버튼
        binding.btnAddRecord.setOnClickListener {
            startActivity(Intent(this, AddRecordActivity::class.java))
        }

        // 요일 클릭 이벤트 활성화
        setupDayClickEvents()

        // 하단 네비게이션
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadMedicineList()   // 홈 화면 돌아오면 전체 기록 다시 표시
    }

    /** 오늘 제목 표시 */
    private fun setTodayHeader() {
        binding.txtTodayTitle.text = "오늘의 복용 기록"
    }

    /** 오늘 요일 index 계산 */
    private fun detectTodayIndex() {
        val calendar = Calendar.getInstance()
        todayIndex = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }

    /** 요일 색상 초기화 + 오늘 요일 강조 */
    private fun colorTodayTextOnly() {
        dayList.forEach {
            it.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        dayList[todayIndex].setTextColor(ContextCompat.getColor(this, R.color.nav_selected))
    }

    /** 오늘 요일 하이라이트 이동 */
    private fun moveHighlightToToday() {
        val target = dayList[todayIndex]
        binding.highlightView.post {
            binding.highlightView.x =
                target.x + target.width / 2 - binding.highlightView.width / 2
        }
    }

    /** 현재 로그인한 사용자 기록 로딩 */
    private fun loadMedicineList() {
        val pref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val userEmail = pref.getString("user_email", null)

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getInstance(this@MainActivity).medicineDao()
            val list = dao.getAllByUser(userEmail)

            runOnUiThread {
                binding.medicineListView.adapter = MedicineAdapter(this@MainActivity, list)
            }
        }
    }

    /** 테스트용 더미 기록 삽입 — 필요 시 구현 */
    private fun insertDummyRecordsOnce() { }

    /** 요일 클릭 이벤트 */
    private fun setupDayClickEvents() {
        dayList.forEachIndexed { index, textView ->
            textView.setOnClickListener {

                // 오늘 요일만 색 강조는 유지
                colorTodayTextOnly()

                // 하이라이트 요일 이동
                binding.highlightView.post {
                    binding.highlightView.x =
                        textView.x + textView.width / 2 - binding.highlightView.width / 2
                }

                // 선택한 요일 기록 로딩
                loadRecordsByWeekday(index)
            }
        }
    }

    /** 특정 요일 기록 로딩 (현재 로그인한 사용자 기준) */
    private fun loadRecordsByWeekday(weekdayIndex: Int) {

        val pref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val userEmail = pref.getString("user_email", null)

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {

            val calendar = Calendar.getInstance()

            // 미래 요일은 기록 없음
            if (weekdayIndex > todayIndex) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "아직 해당 요일의 기록이 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            // 이번 주 월요일 계산
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val diffToMonday =
                (calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
            calendar.add(Calendar.DAY_OF_YEAR, -diffToMonday)

            // 클릭한 요일 날짜 이동
            calendar.add(Calendar.DAY_OF_YEAR, weekdayIndex)
            val startOfDay = calendar.timeInMillis

            // 해당 날 끝
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val endOfDay = calendar.timeInMillis - 1

            val dao = AppDatabase.getInstance(this@MainActivity).medicineDao()
            val records = dao.getRecordsByDayForUser(startOfDay, endOfDay, userEmail)

            runOnUiThread {

                // 🔥 리스트뷰 먼저 초기화 (중첩 표시 방지)
                binding.medicineListView.adapter = null

                if (records.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "해당 요일 기록이 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@runOnUiThread
                }

                // 🔥 UI가 안정된 후 어댑터 적용 → 100% 확정 반영
                binding.medicineListView.post {
                    binding.medicineListView.adapter =
                        MedicineAdapter(this@MainActivity, records)
                }
            }
        }
    }

    /** 날짜 포맷 함수 (사용 시 참고) */
    private fun formatDate(time: Long): String {
        return SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(time)
    }

    /** 하단 네비게이션 처리 */
    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> true

                R.id.nav_report -> {
                    startActivity(Intent(this, WeeklyReportActivity::class.java))
                    false
                }

                R.id.nav_setting -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    false
                }

                else -> false
            }
        }
    }
}
