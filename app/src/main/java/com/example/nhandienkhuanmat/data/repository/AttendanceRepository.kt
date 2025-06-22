package com.example.nhandienkhuanmat.data.repository

import com.example.nhandienkhuanmat.data.local.AttendanceDao
import com.example.nhandienkhuanmat.data.model.Attendance
import com.example.nhandienkhuanmat.data.model.AttendanceWithDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao
) {
    suspend fun insertAttendance(attendance: Attendance): Long {
        return attendanceDao.insertAttendance(attendance)
    }

    suspend fun updateAttendance(attendance: Attendance) {
        attendanceDao.updateAttendance(attendance)
    }

    fun getAttendanceDetailsForLop(lopId: Long): Flow<List<AttendanceWithDetails>> {
        return attendanceDao.getAttendanceDetailsForLop(lopId)
    }

    fun getAttendanceDetailsForUser(userId: Long): Flow<List<AttendanceWithDetails>> {
        return attendanceDao.getAttendanceDetailsForUser(userId)
    }

    suspend fun getCurrentSession(userId: Long): Attendance? {
        return attendanceDao.getCurrentSession(userId)
    }
} 