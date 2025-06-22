package com.example.nhandienkhuanmat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhandienkhuanmat.data.model.Lop
import com.example.nhandienkhuanmat.data.model.LopWithUsers
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.data.model.UserWithLops
import com.example.nhandienkhuanmat.data.model.UserLopCrossRef
import com.example.nhandienkhuanmat.data.model.AttendanceWithDetails
import com.example.nhandienkhuanmat.data.repository.LopRepository
import com.example.nhandienkhuanmat.data.repository.UserRepository
import com.example.nhandienkhuanmat.data.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val lopRepository: LopRepository,
    private val userRepository: UserRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _lops = MutableStateFlow<List<Lop>>(emptyList())
    val lops: StateFlow<List<Lop>> = _lops.asStateFlow()

    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Idle)
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private val _selectedLop = MutableStateFlow<LopWithUsers?>(null)
    val selectedLop: StateFlow<LopWithUsers?> = _selectedLop.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _usersWithLops = MutableStateFlow<List<UserWithLops>>(emptyList())
    val usersWithLops: StateFlow<List<UserWithLops>> = _usersWithLops.asStateFlow()

    private val _attendanceDetails = MutableStateFlow<List<AttendanceWithDetails>>(emptyList())
    val attendanceDetails: StateFlow<List<AttendanceWithDetails>> = _attendanceDetails.asStateFlow()

    init {
        loadLops()
        loadAllUsers()
        loadUsersWithLops()
    }

    private fun loadLops() {
        viewModelScope.launch {
            lopRepository.getAllLops().collect {
                _lops.value = it
            }
        }
    }

    private fun loadAllUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect {
                _allUsers.value = it
            }
        }
    }

    private fun loadUsersWithLops() {
        viewModelScope.launch {
            userRepository.getUsersWithLops().collect {
                _usersWithLops.value = it
            }
        }
    }

    fun loadLopDetails(lopId: Long) {
        viewModelScope.launch {
            lopRepository.getLopWithUsers(lopId).collect {
                _selectedLop.value = it
            }
        }
    }

    fun loadAttendanceDetails(lopId: Long) {
        viewModelScope.launch {
            attendanceRepository.getAttendanceDetailsForLop(lopId).collect {
                _attendanceDetails.value = it
            }
        }
    }

    fun createLop(name: String, description: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AdminUiState.Loading
                val newLop = Lop(name = name, description = description)
                lopRepository.insertLop(newLop)
                _uiState.value = AdminUiState.Success("Tạo lớp thành công")
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun updateLop(lop: Lop) {
        viewModelScope.launch {
            try {
                _uiState.value = AdminUiState.Loading
                lopRepository.updateLop(lop)
                _uiState.value = AdminUiState.Success("Cập nhật lớp thành công")
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun deleteLop(lop: Lop) {
        viewModelScope.launch {
            try {
                _uiState.value = AdminUiState.Loading
                lopRepository.deleteLop(lop)
                _uiState.value = AdminUiState.Success("Xóa lớp thành công")
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun addUserToLop(userId: Long, lopId: Long) {
        viewModelScope.launch {
            try {
                lopRepository.addUserToLop(userId, lopId)
                loadLopDetails(lopId)
                loadUsersWithLops()
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun removeUserFromLop(userId: Long, lopId: Long) {
        viewModelScope.launch {
            try {
                lopRepository.removeUserFromLop(userId, lopId)
                loadLopDetails(lopId)
                loadUsersWithLops()
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = AdminUiState.Idle
    }
}

sealed class AdminUiState {
    object Idle : AdminUiState()
    object Loading : AdminUiState()
    data class Success(val message: String) : AdminUiState()
    data class Error(val message: String) : AdminUiState()
}