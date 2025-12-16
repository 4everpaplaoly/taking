package com.example.taking

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_table")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val symptom: String,
    val timestamp: Long,
    val userEmail: String   // 로그인한 사용자 구분
)
