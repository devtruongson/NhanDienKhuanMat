package com.example.nhandienkhuanmat.presentation.screens

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nhandienkhuanmat.presentation.components.CameraComponent
import com.example.nhandienkhuanmat.presentation.viewmodel.AttendanceViewModel
import com.example.nhandienkhuanmat.presentation.viewmodel.AttendanceState

@Composable
fun AttendanceScreen(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val attendanceState by viewModel.attendanceState.collectAsState()
    val currentSession by viewModel.currentSession.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Điểm danh khuôn mặt",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                currentSession?.let { session ->
                    Text(
                        text = "Phiên hiện tại: ${if (session.checkOutTime == null) "Đang làm việc" else "Đã kết thúc"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Camera and Status
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            CameraComponent(
                onImageCaptured = { bitmap: Bitmap ->
                    viewModel.processFaceForAttendance(bitmap)
                },
                onError = { error ->
                    // Handle camera error
                },
                modifier = Modifier.fillMaxSize()
            )

            // Status overlay
            val state = attendanceState
            if (state !is AttendanceState.Idle) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                ) {
                    when (state) {
                        is AttendanceState.Processing -> {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Đang xử lý...")
                            }
                        }
                        
                        is AttendanceState.FaceRecognized -> {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Xin chào, ${state.user.name}!",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Khoảng cách: ${String.format("%.4f", state.distance)} (Càng thấp càng tốt)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        is AttendanceState.CheckInSuccess -> {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Điểm danh thành công!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Thời gian: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(state.attendance.checkInTime))}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        is AttendanceState.CheckOutSuccess -> {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Đăng xuất thành công!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                state.attendance.checkOutTime?.let { checkOutTime ->
                                    Text(
                                        text = "Thời gian: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(checkOutTime))}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        
                        is AttendanceState.Error -> {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Lỗi",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = state.message,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        else -> { /* Idle state */ }
                    }
                }
            }
        }

        // Instructions
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Hướng dẫn:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Đặt khuôn mặt vào khung hình",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "• Nhấn nút 'Chụp' để điểm danh",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "• Đảm bảo ánh sáng đủ sáng",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // Auto-reset state after delay
    LaunchedEffect(attendanceState) {
        if (attendanceState !is AttendanceState.Idle && attendanceState !is AttendanceState.Processing) {
            kotlinx.coroutines.delay(3000)
            viewModel.resetState()
        }
    }
} 