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
import com.example.nhandienkhuanmat.presentation.viewmodel.AttendanceState
import com.example.nhandienkhuanmat.presentation.viewmodel.AttendanceViewModel
import kotlinx.coroutines.delay

@Composable
fun AttendanceScreen(
    lopId: Long,
    modifier: Modifier = Modifier,
    onAttendanceSuccess: () -> Unit,
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val attendanceState by viewModel.attendanceState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        CameraComponent(
            onImageCaptured = { bitmap: Bitmap ->
                viewModel.processAttendance(bitmap, lopId)
            },
            onError = { error ->
                // The ViewModel can also be made to handle this
            },
            modifier = Modifier.fillMaxSize()
        )

        // Status overlay
        when (val state = attendanceState) {
            is AttendanceState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().align(Alignment.Center)) {
                    CircularProgressIndicator()
                }
            }
            is AttendanceState.Success -> {
                StatusCard(
                    message = state.message,
                    isError = false
                )
            }
            is AttendanceState.Error -> {
                StatusCard(
                    message = state.message,
                    isError = true
                )
            }
            is AttendanceState.Idle -> {
                // Do nothing
            }
        }
    }

    // Auto-reset state after delay
    LaunchedEffect(attendanceState) {
        if (attendanceState is AttendanceState.Success) {
            delay(2000) // Show success message for 2 seconds
            onAttendanceSuccess()
            viewModel.resetState()
        } else if (attendanceState is AttendanceState.Error) {
            delay(3000) // Show error for 3 seconds
            viewModel.resetState()
        }
    }
}

@Composable
fun BoxScope.StatusCard(message: String, isError: Boolean) {
    Card(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
