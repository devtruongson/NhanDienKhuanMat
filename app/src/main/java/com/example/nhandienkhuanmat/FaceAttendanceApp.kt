package com.example.nhandienkhuanmat

import android.app.Application
import com.example.nhandienkhuanmat.data.repository.DataInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FaceAttendanceApp : Application() {
    
    @Inject
    lateinit var dataInitializer: DataInitializer
    
    override fun onCreate() {
        super.onCreate()
        // Initialize demo data
        dataInitializer.initializeDemoData()
    }
} 