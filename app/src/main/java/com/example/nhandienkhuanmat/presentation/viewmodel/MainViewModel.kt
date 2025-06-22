package com.example.nhandienkhuanmat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhandienkhuanmat.data.model.AttendanceWithDetails
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.data.model.UserRole
import com.example.nhandienkhuanmat.data.model.UserWithLops
import com.example.nhandienkhuanmat.data.repository.AttendanceRepository
import com.example.nhandienkhuanmat.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _userWithLops = MutableStateFlow<UserWithLops?>(null)
    val userWithLops: StateFlow<UserWithLops?> = _userWithLops.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userAttendanceHistory = MutableStateFlow<List<AttendanceWithDetails>>(emptyList())
    val userAttendanceHistory: StateFlow<List<AttendanceWithDetails>> = _userAttendanceHistory.asStateFlow()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val newUser = User(
                    name = name,
                    email = email,
                    password = password,
                    role = UserRole.USER // Default role for new users
                )
                userRepository.insertUser(newUser)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val user = userRepository.getUserByEmail(email)
            if (user != null && user.password == password) {
                _currentUser.value = user
                if (user.role == UserRole.USER) {
                    _userWithLops.value = userRepository.getUserWithLops(user.id).first()
                }
                _isLoggedIn.value = true
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        _userWithLops.value = null
        _userAttendanceHistory.value = emptyList()
    }

    fun loadUserAttendanceHistory() {
        _currentUser.value?.id?.let { userId ->
            viewModelScope.launch {
                // The collect will keep observing changes from the Flow
                attendanceRepository.getAttendanceDetailsForUser(userId).collect {
                    _userAttendanceHistory.value = it
                }
            }
        }
    }

    fun isAdmin(): Boolean {
        return currentUser.value?.role == UserRole.ADMIN
    }

    fun isUser(): Boolean {
        return currentUser.value?.role == UserRole.USER
    }
} 