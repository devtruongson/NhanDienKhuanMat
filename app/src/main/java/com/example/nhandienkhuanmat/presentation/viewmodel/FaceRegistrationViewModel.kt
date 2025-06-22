package com.example.nhandienkhuanmat.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhandienkhuanmat.domain.service.FaceRecognitionService
import com.example.nhandienkhuanmat.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceRegistrationViewModel @Inject constructor(
    private val faceRecognitionService: FaceRecognitionService,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun registerFace(bitmap: Bitmap, userId: Long) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val user = userRepository.getUserById(userId)
                if (user == null) {
                    _registrationState.value = RegistrationState.Error("Không tìm thấy người dùng.")
                    return@launch
                }

                val faces = faceRecognitionService.detectFaces(bitmap)
                if (faces.isEmpty()) {
                    _registrationState.value = RegistrationState.Error("Không tìm thấy khuôn mặt trong ảnh.")
                    return@launch
                }
                if (faces.size > 1) {
                    _registrationState.value = RegistrationState.Error("Phát hiện nhiều hơn một khuôn mặt.")
                    return@launch
                }

                val face = faces.first()
                val faceBitmap = faceRecognitionService.cropFace(bitmap, face.boundingBox)
                val embedding = faceRecognitionService.extractFaceEmbeddings(faceBitmap)

                user.faceEmbedding = embedding
                userRepository.updateUser(user)

                _registrationState.value = RegistrationState.Success
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Đã xảy ra lỗi không xác định")
            }
        }
    }

    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }
} 