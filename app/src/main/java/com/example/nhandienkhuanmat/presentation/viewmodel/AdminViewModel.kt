package com.example.nhandienkhuanmat.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhandienkhuanmat.data.model.Lop
import com.example.nhandienkhuanmat.data.model.UserWithLops
import com.example.nhandienkhuanmat.data.repository.LopRepository
import com.example.nhandienkhuanmat.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val lopRepository: LopRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _lops = MutableStateFlow<List<Lop>>(emptyList())
    val lops: StateFlow<List<Lop>> = _lops.asStateFlow()

    private val _usersWithLops = MutableStateFlow<List<UserWithLops>>(emptyList())
    val usersWithLops: StateFlow<List<UserWithLops>> = _usersWithLops.asStateFlow()

    init {
        fetchAllLops()
        fetchAllUsersWithLops()
    }

    private fun fetchAllLops() {
        viewModelScope.launch {
            lopRepository.getAllLops()
                .catch { e ->
                    Log.e("AdminViewModel", "fetchAllLops failed", e)
                }
                .collect { lopsList ->
                    _lops.value = lopsList
                }
        }
    }

    private fun fetchAllUsersWithLops() {
        viewModelScope.launch {
            userRepository.getUsersWithLops()
                .catch { exception ->
                    Log.e("AdminViewModel", "fetchAllUsersWithLops failed", exception)
                }
                .collect { userList ->
                    _usersWithLops.value = userList
                }
        }
    }

    fun createLop(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                val newLop = Lop(name = name)
                lopRepository.insertLop(newLop)
            }
        }
    }

    fun addUserToLop(userId: Long, lopId: Long) {
        viewModelScope.launch {
            lopRepository.addUserToLop(userId, lopId)
        }
    }

    fun removeUserFromLop(userId: Long, lopId: Long) {
        viewModelScope.launch {
            lopRepository.removeUserFromLop(userId, lopId)
        }
    }
}