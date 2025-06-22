package com.example.nhandienkhuanmat.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UserWithLops(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entity = Lop::class,
        entityColumn = "id",
        associateBy = Junction(
            value = UserLopCrossRef::class,
            parentColumn = "userId",
            entityColumn = "lopId"
        )
    )
    val lops: List<Lop>
) 