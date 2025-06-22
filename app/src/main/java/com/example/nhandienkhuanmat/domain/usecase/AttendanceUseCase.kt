package com.example.nhandienkhuanmat.domain.usecase

import com.example.nhandienkhuanmat.data.model.Attendance
import com.example.nhandienkhuanmat.data.model.AttendanceStatus
import com.example.nhandienkhuanmat.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) {
    fun getAllAttendance(): Flow<List<Attendance>> = attendanceRepository.getAllAttendance()

    fun getAttendanceByUserId(userId: Long): Flow<List<Attendance>> = attendanceRepository.getAttendanceByUserId(userId)

    fun getAttendanceByDate(date: String): Flow<List<Attendance>> = attendanceRepository.getAttendanceByDate(date)

    suspend fun checkIn(userId: Long, lopId: Long): AttendanceResult {
        val today = getCurrentDate()
        val currentTime = System.currentTimeMillis()
        
        // Check if already checked in today
        val existingAttendance = attendanceRepository.getAttendanceByUserIdAndDateAndLopId(userId, today, lopId)
        if (existingAttendance != null && existingAttendance.checkOutTime == null) {
            return AttendanceResult.AlreadyCheckedIn
        }

        // Determine if late (assuming work starts at 8:00 AM)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val isLate = calendar.get(Calendar.HOUR_OF_DAY) > 8 || 
                    (calendar.get(Calendar.HOUR_OF_DAY) == 8 && calendar.get(Calendar.MINUTE) > 0)

        val status = if (isLate) AttendanceStatus.LATE else AttendanceStatus.PRESENT

        val attendance = Attendance(
            userId = userId,
            lopId = lopId,
            checkInTime = currentTime,
            date = today,
            status = status
        )

        val attendanceId = attendanceRepository.insertAttendance(attendance)
        return AttendanceResult.CheckInSuccess(attendance.copy(id = attendanceId))
    }

    suspend fun checkOut(userId: Long): AttendanceResult {
        val currentSession = attendanceRepository.getCurrentSession(userId)
        if (currentSession == null) {
            return AttendanceResult.NoActiveSession
        }

        val updatedAttendance = currentSession.copy(
            checkOutTime = System.currentTimeMillis()
        )
        attendanceRepository.updateAttendance(updatedAttendance)
        return AttendanceResult.CheckOutSuccess(updatedAttendance)
    }

    suspend fun getCurrentSession(userId: Long): Attendance? = attendanceRepository.getCurrentSession(userId)

    suspend fun getAttendanceStats(date: String): AttendanceStats {
        val attendanceList = attendanceRepository.getAttendanceByDate(date).first()
        
        val presentCount = attendanceList.count { it.status == AttendanceStatus.PRESENT }
        val lateCount = attendanceList.count { it.status == AttendanceStatus.LATE }
        val absentCount = attendanceList.count { it.status == AttendanceStatus.ABSENT }
        val totalCount = presentCount + lateCount + absentCount

        return AttendanceStats(
            date = date,
            total = totalCount,
            present = presentCount,
            late = lateCount,
            absent = absentCount
        )
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}

sealed class AttendanceResult {
    object AlreadyCheckedIn : AttendanceResult()
    object NoActiveSession : AttendanceResult()
    data class CheckInSuccess(val attendance: Attendance) : AttendanceResult()
    data class CheckOutSuccess(val attendance: Attendance) : AttendanceResult()
}

data class AttendanceStats(
    val date: String,
    val total: Int,
    val present: Int,
    val late: Int,
    val absent: Int
) 