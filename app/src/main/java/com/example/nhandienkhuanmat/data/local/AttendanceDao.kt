package com.example.nhandienkhuanmat.data.local

import androidx.room.*
import com.example.nhandienkhuanmat.data.model.Attendance
import com.example.nhandienkhuanmat.data.model.AttendanceWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance): Long

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Transaction
    @Query("SELECT * FROM attendance WHERE lopId = :lopId ORDER BY checkInTime DESC")
    fun getAttendanceDetailsForLop(lopId: Long): Flow<List<AttendanceWithDetails>>

    @Transaction
    @Query("SELECT * FROM attendance WHERE userId = :userId ORDER BY checkInTime DESC")
    fun getAttendanceDetailsForUser(userId: Long): Flow<List<AttendanceWithDetails>>

    @Query("SELECT * FROM attendance WHERE userId = :userId AND checkOutTime IS NULL ORDER BY checkInTime DESC LIMIT 1")
    suspend fun getCurrentSession(userId: Long): Attendance?
} 