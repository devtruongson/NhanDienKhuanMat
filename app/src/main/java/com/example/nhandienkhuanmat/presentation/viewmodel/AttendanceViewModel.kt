package com.example.nhandienkhuanmat.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhandienkhuanmat.data.model.Attendance
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.domain.usecase.AttendanceUseCase
import com.example.nhandienkhuanmat.domain.usecase.FaceRecognitionResult
import com.example.nhandienkhuanmat.domain.usecase.FaceRecognitionUseCase
import com.example.nhandienkhuanmat.domain.usecase.AttendanceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceUseCase: AttendanceUseCase,
    private val faceRecognitionUseCase: FaceRecognitionUseCase
) : ViewModel() {

    private val _attendanceState = MutableStateFlow<AttendanceState>(AttendanceState.Idle)
    val attendanceState: StateFlow<AttendanceState> = _attendanceState.asStateFlow()

    private val _currentSession = MutableStateFlow<Attendance?>(null)
    val currentSession: StateFlow<Attendance?> = _currentSession.asStateFlow()

    private val _attendanceList = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceList: StateFlow<List<Attendance>> = _attendanceList.asStateFlow()

    fun processFaceForAttendance(bitmap: Bitmap, lopId: Long) {
        _attendanceState.value = AttendanceState.Processing
        viewModelScope.launch {
            try {
                when (val result = faceRecognitionUseCase.detectAndRecognizeFace(bitmap)) {
                    is FaceRecognitionResult.FaceRecognized -> {
                        _attendanceState.value = AttendanceState.FaceRecognized(result.user, result.distance)
                        processAttendanceForUser(result.user.id, lopId)
                    }
                    is FaceRecognitionResult.NoFaceDetected -> {
                        _attendanceState.value = AttendanceState.Error("Không phát hiện khuôn mặt")
                    }
                    is FaceRecognitionResult.FaceNotRecognized -> {
                        _attendanceState.value = AttendanceState.Error("Khuôn mặt không được nhận diện")
                    }
                    is FaceRecognitionResult.Error -> {
                        _attendanceState.value = AttendanceState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _attendanceState.value = AttendanceState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    private fun processAttendanceForUser(userId: Long, lopId: Long) {
        viewModelScope.launch {
            try {
                val currentSession = attendanceUseCase.getCurrentSession(userId)
                if (currentSession == null) {
                    // Check in
                    when (val result = attendanceUseCase.checkIn(userId, lopId)) {
                        is AttendanceResult.CheckInSuccess -> {
                            _currentSession.value = result.attendance
                            _attendanceState.value = AttendanceState.CheckInSuccess(result.attendance)
                        }
                        is AttendanceResult.AlreadyCheckedIn -> {
                            _attendanceState.value = AttendanceState.Error("Đã điểm danh vào hôm nay")
                        }
                        else -> {
                            _attendanceState.value = AttendanceState.Error("Lỗi điểm danh")
                        }
                    }
                } else {
                    // Check out
                    when (val result = attendanceUseCase.checkOut(userId)) {
                        is AttendanceResult.CheckOutSuccess -> {
                            _currentSession.value = null
                            _attendanceState.value = AttendanceState.CheckOutSuccess(result.attendance)
                        }
                        is AttendanceResult.NoActiveSession -> {
                            _attendanceState.value = AttendanceState.Error("Không có phiên làm việc đang hoạt động")
                        }
                        else -> {
                             _attendanceState.value = AttendanceState.Error("Lỗi đăng xuất")
                        }
                    }
                }
            } catch (e: Exception) {
                _attendanceState.value = AttendanceState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun loadAttendanceByDate(date: String) {
        viewModelScope.launch {
            try {
                attendanceUseCase.getAttendanceByDate(date).collect { attendanceList ->
                    _attendanceList.value = attendanceList
                }
            } catch (e: Exception) {
                _attendanceState.value = AttendanceState.Error(e.message ?: "Lỗi tải dữ liệu")
            }
        }
    }

    fun resetState() {
        _attendanceState.value = AttendanceState.Idle
    }
}

sealed class AttendanceState {
    object Idle : AttendanceState()
    object Processing : AttendanceState()
    data class FaceRecognized(val user: User, val distance: Float) : AttendanceState()
    data class CheckInSuccess(val attendance: Attendance) : AttendanceState()
    data class CheckOutSuccess(val attendance: Attendance) : AttendanceState()
    data class Error(val message: String) : AttendanceState()
} 