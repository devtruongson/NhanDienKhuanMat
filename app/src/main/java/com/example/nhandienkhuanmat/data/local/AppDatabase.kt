package com.example.nhandienkhuanmat.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.nhandienkhuanmat.data.model.Attendance
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.data.model.Lop
import com.example.nhandienkhuanmat.data.model.UserLopCrossRef
import com.example.nhandienkhuanmat.data.local.converter.Converters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nhandienkhuanmat.data.model.UserWithLops

@Database(
    entities = [User::class, Attendance::class, Lop::class, UserLopCrossRef::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun lopDao(): LopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "face_attendance_database"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 