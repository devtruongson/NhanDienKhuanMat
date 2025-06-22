package com.example.nhandienkhuanmat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val lopId: Long,
    val checkInTime: Long = System.currentTimeMillis(),
    var checkOutTime: Long? = null,
    val status: AttendanceStatus
)

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE
}