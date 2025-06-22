package com.example.nhandienkhuanmat.presentation.screens

import androidx.compose.foundation.clickable
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
import com.example.nhandienkhuanmat.data.model.Lop
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.data.model.UserWithLops
import com.example.nhandienkhuanmat.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val usersWithLops by viewModel.usersWithLops.collectAsState()
    val allLops by viewModel.lops.collectAsState()
    var selectedUserWithLops by remember { mutableStateOf<UserWithLops?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý người dùng") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        
        selectedUserWithLops?.let { user ->
            AssignLopsDialog(
                userWithLops = user,
                allLops = allLops,
                onDismiss = { selectedUserWithLops = null },
                onConfirmToggle = { lop, isEnrolled ->
                    if (isEnrolled) {
                        viewModel.addUserToLop(user.user.id, lop.id)
                    } else {
                        viewModel.removeUserFromLop(user.user.id, lop.id)
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(usersWithLops) { userWithLops ->
                UserItem(
                    userWithLops = userWithLops,
                    onClick = { selectedUserWithLops = userWithLops }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun UserItem(userWithLops: UserWithLops, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = userWithLops.user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = userWithLops.user.email, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Lớp: ${userWithLops.lops.joinToString { it.name }.ifEmpty { "Chưa có" }}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text("Chỉnh sửa")
    }
}

@Composable
fun AssignLopsDialog(
    userWithLops: UserWithLops,
    allLops: List<Lop>,
    onDismiss: () -> Unit,
    onConfirmToggle: (Lop, Boolean) -> Unit
) {
    val userLopIds = remember(userWithLops) { userWithLops.lops.map { it.id }.toSet() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gán lớp cho ${userWithLops.user.name}") },
        text = {
            LazyColumn {
                items(allLops) { lop ->
                    var isChecked by remember(userLopIds) { mutableStateOf(lop.id in userLopIds) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val shouldEnroll = !isChecked
                                onConfirmToggle(lop, shouldEnroll)
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { newCheckedState ->
                                onConfirmToggle(lop, newCheckedState)
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = lop.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Xong")
            }
        }
    )
}
