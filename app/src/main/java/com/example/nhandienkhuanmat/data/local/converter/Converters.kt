package com.example.nhandienkhuanmat.data.local.converter

import androidx.room.TypeConverter
import com.example.nhandienkhuanmat.data.model.AttendanceStatus
import com.example.nhandienkhuanmat.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(role: String): UserRole {
        return UserRole.valueOf(role)
    }

    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus): String {
        return status.name
    }

    @TypeConverter
    fun toAttendanceStatus(status: String): AttendanceStatus {
        return AttendanceStatus.valueOf(status)
    }
} 