package com.example.nhandienkhuanmat.data.repository

import com.example.nhandienkhuanmat.data.local.UserDao
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.data.model.UserRole
import com.example.nhandienkhuanmat.data.model.UserWithLops
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    fun getUsersWithLops(): Flow<List<UserWithLops>> = userDao.getUsersWithLops()

    fun getUserWithLops(userId: Long): Flow<UserWithLops?> = userDao.getUserWithLops(userId)

    fun getUsersByRole(role: UserRole): Flow<List<User>> = userDao.getUsersByRole(role.name)

    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun insertUser(user: User): Long = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    suspend fun deleteUserById(userId: Long) = userDao.deleteUserById(userId)

    suspend fun getUserCountByRole(role: UserRole): Int = userDao.getUserCountByRole(role.name)
} 