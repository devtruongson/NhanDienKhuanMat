package com.example.nhandienkhuanmat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val checkInTime: Long,
    val checkOutTime: Long? = null,
    val date: String, // Format: yyyy-MM-dd
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val location: String? = null,
    val notes: String? = null
)

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE,
    HALF_DAY
} 