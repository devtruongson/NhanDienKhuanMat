package com.example.nhandienkhuanmat.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class AttendanceWithDetails(
    @Embedded
    val attendance: Attendance,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: User,

    @Relation(
        parentColumn = "lopId",
        entityColumn = "id"
    )
    val lop: Lop
) 