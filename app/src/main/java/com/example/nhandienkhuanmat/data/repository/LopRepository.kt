package com.example.nhandienkhuanmat.data.repository

import com.example.nhandienkhuanmat.data.local.LopDao
import com.example.nhandienkhuanmat.data.model.Lop
import com.example.nhandienkhuanmat.data.model.LopWithUsers
import com.example.nhandienkhuanmat.data.model.UserLopCrossRef
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LopRepository @Inject constructor(private val lopDao: LopDao) {

    fun getAllLops(): Flow<List<Lop>> = lopDao.getAllLops()

    fun getLopWithUsers(lopId: Long): Flow<LopWithUsers> = lopDao.getUsersInLop(lopId)

    suspend fun insertLop(lop: Lop) {
        lopDao.insertLop(lop)
    }

    suspend fun updateLop(lop: Lop) {
        lopDao.updateLop(lop)
    }

    suspend fun deleteLop(lop: Lop) {
        lopDao.deleteLop(lop)
    }

    suspend fun addUserToLop(userId: Long, lopId: Long) {
        lopDao.addUserToLop(UserLopCrossRef(userId, lopId))
    }

    suspend fun removeUserFromLop(userId: Long, lopId: Long) {
        lopDao.removeUserFromLop(UserLopCrossRef(userId, lopId))
    }
}
