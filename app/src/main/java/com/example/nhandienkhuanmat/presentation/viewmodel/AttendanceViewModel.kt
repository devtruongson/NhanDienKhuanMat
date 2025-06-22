package com.example.nhandienkhuanmat.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhandienkhuanmat.data.model.Attendance
import com.example.nhandienkhuanmat.data.model.AttendanceStatus
import com.example.nhandienkhuanmat.data.repository.AttendanceRepository
import com.example.nhandienkhuanmat.domain.usecase.FaceRecognitionResult
import com.example.nhandienkhuanmat.domain.usecase.FaceRecognitionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val faceRecognitionUseCase: FaceRecognitionUseCase,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _attendanceState = MutableStateFlow<AttendanceState>(AttendanceState.Idle)
    val attendanceState: StateFlow<AttendanceState> = _attendanceState

    fun processAttendance(bitmap: Bitmap, lopId: Long) {
        viewModelScope.launch {
            _attendanceState.value = AttendanceState.Loading
            when (val result = faceRecognitionUseCase.detectAndRecognizeFace(bitmap)) {
                is FaceRecognitionResult.FaceRecognized -> {
                    val userId = result.user.id
                    val currentSession = attendanceRepository.getCurrentSession(userId)

                    if (currentSession == null) {
                        // Check in
                        val newAttendance = Attendance(
                            userId = userId,
                            lopId = lopId,
                            status = AttendanceStatus.PRESENT
                        )
                        attendanceRepository.insertAttendance(newAttendance)
                        _attendanceState.value = AttendanceState.Success("Điểm danh vào thành công!")
                    } else {
                        // Check out
                        val updatedSession = currentSession.copy(checkOutTime = System.currentTimeMillis())
                        attendanceRepository.updateAttendance(updatedSession)
                        _attendanceState.value = AttendanceState.Success("Điểm danh ra thành công!")
                    }
                }
                is FaceRecognitionResult.FaceNotRecognized -> {
                    _attendanceState.value = AttendanceState.Error("Không nhận dạng được khuôn mặt.")
                }
                is FaceRecognitionResult.NoFaceDetected -> {
                    _attendanceState.value = AttendanceState.Error("Không tìm thấy khuôn mặt trong ảnh.")
                }
                is FaceRecognitionResult.Error -> {
                    _attendanceState.value = AttendanceState.Error(result.message)
                }
            }
        }
    }

    fun resetState() {
        _attendanceState.value = AttendanceState.Idle
    }
}

sealed class AttendanceState {
    object Idle : AttendanceState()
    object Loading : AttendanceState()
    data class Success(val message: String) : AttendanceState()
    data class Error(val message: String) : AttendanceState()
} 