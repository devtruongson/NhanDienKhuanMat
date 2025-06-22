package com.example.nhandienkhuanmat.data.local.converter

import androidx.room.TypeConverter
import com.example.nhandienkhuanmat.data.model.AttendanceStatus
import com.example.nhandienkhuanmat.data.model.UserRole
import java.nio.ByteBuffer
import java.nio.FloatBuffer

class Converters {
    @TypeConverter
    fun fromFloatArray(floatArray: FloatArray?): ByteArray? {
        if (floatArray == null) return null
        val byteBuffer = ByteBuffer.allocate(floatArray.size * 4)
        val floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer.put(floatArray)
        return byteBuffer.array()
    }

    @TypeConverter
    fun toFloatArray(byteArray: ByteArray?): FloatArray? {
        if (byteArray == null) return null
        val floatBuffer = ByteBuffer.wrap(byteArray).asFloatBuffer()
        val floatArray = FloatArray(floatBuffer.remaining())
        floatBuffer.get(floatArray)
        return floatArray
    }

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