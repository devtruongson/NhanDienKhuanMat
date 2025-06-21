package com.example.nhandienkhuanmat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val role: UserRole,
    val faceEmbeddings: String? = null, // JSON string of face embeddings
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

enum class UserRole {
    ADMIN,
    USER
} 