package com.example.nhandienkhuanmat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lops")
data class Lop(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) 