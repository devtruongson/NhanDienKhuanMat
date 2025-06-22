package com.example.nhandienkhuanmat.presentation.screens

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nhandienkhuanmat.presentation.components.CameraComponent
import com.example.nhandienkhuanmat.presentation.viewmodel.FaceRegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceRegistrationScreen(
    userId: Long,
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: FaceRegistrationViewModel = hiltViewModel()
) {
    val registrationState by viewModel.registrationState.collectAsState()
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đăng ký khuôn mặt") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (capturedBitmap == null) {
                CameraComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onImageCaptured = { bitmap ->
                        capturedBitmap = bitmap
                    },
                    onError = { errorMessage ->
                        // In a real app, you'd show a Snackbar or Toast
                        println("Camera Error: $errorMessage")
                    }
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Ảnh đã được chụp. Sẵn sàng để đăng ký.")
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (val state = registrationState) {
                is FaceRegistrationViewModel.RegistrationState.Loading -> {
                    CircularProgressIndicator()
                }
                is FaceRegistrationViewModel.RegistrationState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
                is FaceRegistrationViewModel.RegistrationState.Error -> {
                    Text(text = "Lỗi: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { capturedBitmap = null }) {
                        Text("Chụp lại")
                    }
                }
                is FaceRegistrationViewModel.RegistrationState.Idle -> {
                    Button(
                        onClick = {
                            capturedBitmap?.let {
                                viewModel.registerFace(it, userId)
                            }
                        },
                        enabled = capturedBitmap != null
                    ) {
                        Text("Đăng ký khuôn mặt")
                    }
                }
            }
        }
    }
}