package com.example.nhandienkhuanmat.data.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["userId", "lopId"],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["lopId"])
    ]
)
data class UserLopCrossRef(
    val userId: Long,
    val lopId: Long
) 