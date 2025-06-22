package com.example.nhandienkhuanmat.di

import android.content.Context
import com.example.nhandienkhuanmat.data.local.AppDatabase
import com.example.nhandienkhuanmat.data.local.AttendanceDao
import com.example.nhandienkhuanmat.data.local.UserDao
import com.example.nhandienkhuanmat.data.local.LopDao
import com.example.nhandienkhuanmat.domain.service.FaceRecognitionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideAttendanceDao(database: AppDatabase): AttendanceDao {
        return database.attendanceDao()
    }

    @Provides
    @Singleton
    fun provideLopDao(database: AppDatabase): LopDao {
        return database.lopDao()
    }

    @Provides
    @Singleton
    fun provideFaceRecognitionService( @ApplicationContext context: Context): FaceRecognitionService {
        return FaceRecognitionService(context)
    }
} 