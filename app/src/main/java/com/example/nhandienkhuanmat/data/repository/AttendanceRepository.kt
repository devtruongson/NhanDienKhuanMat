package com.example.nhandienkhuanmat.data.repository

import com.example.nhandienkhuanmat.data.local.AttendanceDao
import com.example.nhandienkhuanmat.data.model.Attendance
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao
) {
    fun getAllAttendance(): Flow<List<Attendance>> = attendanceDao.getAllAttendance()

    fun getAttendanceByUserId(userId: Long): Flow<List<Attendance>> = attendanceDao.getAttendanceByUserId(userId)

    fun getAttendanceByDate(date: String): Flow<List<Attendance>> = attendanceDao.getAttendanceByDate(date)

    suspend fun getAttendanceByUserIdAndDateAndLopId(userId: Long, date: String, lopId: Long): Attendance? =
        attendanceDao.getAttendanceByUserIdAndDateAndLopId(userId, date, lopId)

    suspend fun getCurrentSession(userId: Long): Attendance? = attendanceDao.getCurrentSession(userId)

    suspend fun insertAttendance(attendance: Attendance): Long = attendanceDao.insertAttendance(attendance)

    suspend fun updateAttendance(attendance: Attendance) = attendanceDao.updateAttendance(attendance)

    suspend fun deleteAttendance(attendance: Attendance) = attendanceDao.deleteAttendance(attendance)

    suspend fun deleteAttendanceById(attendanceId: Long) = attendanceDao.deleteAttendanceById(attendanceId)

    suspend fun getAttendanceCountByDateAndStatus(date: String, status: String): Int = 
        attendanceDao.getAttendanceCountByDateAndStatus(date, status)
} 