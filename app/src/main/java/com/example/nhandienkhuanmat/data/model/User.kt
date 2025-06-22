package com.example.nhandienkhuanmat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole,
    var faceEmbedding: FloatArray? = null
)

enum class UserRole {
    ADMIN,
    USER
} 