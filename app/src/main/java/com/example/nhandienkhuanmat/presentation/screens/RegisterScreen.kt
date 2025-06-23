package com.example.nhandienkhuanmat.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onRegister: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tạo tài khoản", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Họ và tên") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Validate fields
                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    errorMessage = "Vui lòng nhập đầy đủ thông tin."
                    return@Button
                }
                // Simple email format check
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = "Email không hợp lệ."
                    return@Button
                }
                if (password.length < 6) {
                    errorMessage = "Mật khẩu phải có ít nhất 6 ký tự."
                    return@Button
                }
                onRegister(name, email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đăng ký")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Đã có tài khoản? Đăng nhập")
        }

        // Popup for error message
        if (errorMessage != null) {
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                title = { Text("Thông báo") },
                text = { Text(errorMessage ?: "") },
                confirmButton = {
                    Button(onClick = { errorMessage = null }) {
                        Text("Đóng")
                    }
                }
            )
        }
    }
} 