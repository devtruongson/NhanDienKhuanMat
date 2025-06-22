package com.example.nhandienkhuanmat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.data.model.UserRole
import com.example.nhandienkhuanmat.data.model.UserWithLops
import com.example.nhandienkhuanmat.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _userWithLops = MutableStateFlow<UserWithLops?>(null)
    val userWithLops: StateFlow<UserWithLops?> = _userWithLops.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun register(name: String, email: String) {
        viewModelScope.launch {
            try {
                val newUser = User(
                    name = name,
                    email = email,
                    role = UserRole.USER // Default role for new users
                )
                userRepository.insertUser(newUser)
                // Optionally, you can add some user feedback mechanism here
            } catch (e: Exception) {
                // Handle error, e.g., email already exists
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    if (user.role == UserRole.USER) {
                        userRepository.getUserWithLops(user.id).collect {
                            _userWithLops.value = it
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        _userWithLops.value = null
    }

    fun isAdmin(): Boolean {
        return currentUser.value?.role == UserRole.ADMIN
    }

    fun isUser(): Boolean {
        return currentUser.value?.role == UserRole.USER
    }
} 