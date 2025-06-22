package com.example.nhandienkhuanmat.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class LopWithUsers(
    @Embedded val lop: Lop,
    @Relation(
        parentColumn = "id",
        entity = User::class,
        entityColumn = "id",
        associateBy = Junction(
            value = UserLopCrossRef::class,
            parentColumn = "lopId",
            entityColumn = "userId"
        )
    )
    val users: List<User>
) 