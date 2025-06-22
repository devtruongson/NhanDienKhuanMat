package com.example.nhandienkhuanmat.data.local

import androidx.room.*
import com.example.nhandienkhuanmat.data.model.Attendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY checkInTime DESC")
    fun getAllAttendance(): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE userId = :userId ORDER BY checkInTime DESC")
    fun getAttendanceByUserId(userId: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY checkInTime DESC")
    fun getAttendanceByDate(date: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE userId = :userId AND date = :date AND lopId = :lopId LIMIT 1")
    suspend fun getAttendanceByUserIdAndDateAndLopId(userId: Long, date: String, lopId: Long): Attendance?

    @Query("SELECT * FROM attendance WHERE userId = :userId AND checkOutTime IS NULL ORDER BY checkInTime DESC LIMIT 1")
    suspend fun getCurrentSession(userId: Long): Attendance?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance): Long

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("DELETE FROM attendance WHERE id = :attendanceId")
    suspend fun deleteAttendanceById(attendanceId: Long)

    @Query("SELECT COUNT(*) FROM attendance WHERE date = :date AND status = :status")
    suspend fun getAttendanceCountByDateAndStatus(date: String, status: String): Int
} 