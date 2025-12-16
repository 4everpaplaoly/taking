package com.example.taking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MedicineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicine: MedicineEntity)

    // 특정 유저의 전체 기록
    @Query("SELECT * FROM medicine_table WHERE userEmail = :userEmail ORDER BY timestamp DESC")
    suspend fun getAllByUser(userEmail: String): List<MedicineEntity>

    // 특정 날짜 (요일) 기록 조회
    @Query("""
        SELECT * FROM medicine_table 
        WHERE timestamp BETWEEN :start AND :end 
          AND userEmail = :userEmail
        ORDER BY timestamp DESC
    """)
    suspend fun getRecordsByDayForUser(
        start: Long,
        end: Long,
        userEmail: String
    ): List<MedicineEntity>

    // 주간 리포트 조회
    @Query("""
        SELECT * FROM medicine_table
        WHERE timestamp BETWEEN :start AND :end
          AND userEmail = :userEmail
        ORDER BY timestamp DESC
    """)
    suspend fun getByDateRangeForUser(
        start: Long,
        end: Long,
        userEmail: String
    ): List<MedicineEntity>

    // (사용 X 권장) 유저 구분 없는 전체 조회
    @Query("SELECT * FROM medicine_table WHERE timestamp BETWEEN :start AND :end")
    fun getByDateRange(start: Long, end: Long): List<MedicineEntity>
}
