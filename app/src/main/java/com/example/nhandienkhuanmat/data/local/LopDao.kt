package com.example.nhandienkhuanmat.data.local

import androidx.room.*
import com.example.nhandienkhuanmat.data.model.Lop
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.data.model.UserLopCrossRef
import com.example.nhandienkhuanmat.data.model.LopWithUsers
import kotlinx.coroutines.flow.Flow

@Dao
interface LopDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLop(lop: Lop)

    @Update
    suspend fun updateLop(lop: Lop)

    @Delete
    suspend fun deleteLop(lop: Lop)

    @Query("SELECT * FROM lops ORDER BY name ASC")
    fun getAllLops(): Flow<List<Lop>>

    @Query("SELECT * FROM lops WHERE id = :lopId")
    fun getLopById(lopId: Long): Flow<Lop>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserToLop(crossRef: UserLopCrossRef)

    @Delete
    suspend fun removeUserFromLop(crossRef: UserLopCrossRef)

    @Transaction
    @Query("SELECT * FROM lops WHERE id = :lopId")
    fun getUsersInLop(lopId: Long): Flow<LopWithUsers>
} 