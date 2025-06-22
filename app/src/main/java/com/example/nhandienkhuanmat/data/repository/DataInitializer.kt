package com.example.nhandienkhuanmat.data.repository

import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    private val userRepository: UserRepository
) {
    fun initializeDemoData() {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if data already exists
            val adminCount = userRepository.getUserCountByRole(UserRole.ADMIN)
            val userCount = userRepository.getUserCountByRole(UserRole.USER)
            
            if (adminCount == 0 && userCount == 0) {
                // Create demo admin user
                val adminUser = User(
                    name = "Admin User",
                    email = "admin@example.com",
                    password = "password",
                    role = UserRole.ADMIN
                )
                userRepository.insertUser(adminUser)
                
                // Create demo regular user
                val regularUser = User(
                    name = "Regular User",
                    email = "user@example.com",
                    password = "password",
                    role = UserRole.USER
                )
                userRepository.insertUser(regularUser)
            }
        }
    }
} 