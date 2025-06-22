package com.example.nhandienkhuanmat.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailScreen(
    lopId: Long,
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val selectedLopWithUsers by viewModel.selectedLop.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    LaunchedEffect(lopId) {
        viewModel.loadLopDetails(lopId)
    }

    val enrolledUserIds = selectedLopWithUsers?.users?.map { it.id }?.toSet() ?: emptySet()
    val unenrolledUsers = allUsers.filter { it.id !in enrolledUserIds }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedLopWithUsers?.lop?.name ?: "Chi tiết lớp") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Thành viên trong lớp", style = MaterialTheme.typography.headlineSmall)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(selectedLopWithUsers?.users ?: emptyList()) { user ->
                    UserClassManagementItem(
                        user = user,
                        isEnrolled = true,
                        onToggleEnrollment = {
                            viewModel.removeUserFromLop(user.id, lopId)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Thêm thành viên", style = MaterialTheme.typography.headlineSmall)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(unenrolledUsers) { user ->
                    UserClassManagementItem(
                        user = user,
                        isEnrolled = false,
                        onToggleEnrollment = {
                            viewModel.addUserToLop(user.id, lopId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserClassManagementItem(
    user: User,
    isEnrolled: Boolean,
    onToggleEnrollment: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = user.name, modifier = Modifier.weight(1f))
        Button(onClick = onToggleEnrollment) {
            Text(if (isEnrolled) "Xóa" else "Thêm")
        }
    }
} 