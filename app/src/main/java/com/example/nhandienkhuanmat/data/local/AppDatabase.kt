package com.example.nhandienkhuanmat.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.nhandienkhuanmat.data.model.Attendance
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.data.local.converter.Converters

@Database(
    entities = [User::class, Attendance::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "face_attendance_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 