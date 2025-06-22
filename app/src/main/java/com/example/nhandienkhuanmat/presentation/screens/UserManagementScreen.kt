package com.example.nhandienkhuanmat.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    adminViewModel: AdminViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val usersWithLops by adminViewModel.usersWithLops.collectAsState()
    val allLops by adminViewModel.lops.collectAsState()
    var selectedUser by remember { mutableStateOf<UserWithLops?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý người dùng") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("<-") // Back icon
                    }
                }
            )
        }
    ) { padding ->
        if (selectedUser != null) {
            AssignLopsDialog(
                userWithLops = selectedUser!!,
                allLops = allLops,
                onDismiss = { selectedUser = null },
                onConfirm = { user, lop, shouldEnroll ->
                    if (shouldEnroll) {
                        adminViewModel.addUserToLop(user.id, lop.id)
                    } else {
                        adminViewModel.removeUserFromLop(user.id, lop.id)
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(usersWithLops) { userWithLops ->
                UserItem(userWithLops) {
                    selectedUser = userWithLops
                }
                Divider()
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
        Column {
            Text(text = userWithLops.user.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = userWithLops.user.email, style = MaterialTheme.typography.bodySmall)
        }
        Text(text = "Sửa")
    }
}

@Composable
fun AssignLopsDialog(
    userWithLops: UserWithLops,
    allLops: List<Lop>,
    onDismiss: () -> Unit,
    onConfirm: (User, Lop, Boolean) -> Unit
) {
    val userLopIds = remember(userWithLops) { userWithLops.lops.map { it.id }.toSet() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gán lớp cho ${userWithLops.user.name}") },
        text = {
            LazyColumn {
                items(allLops) { lop ->
                    var isChecked by remember { mutableStateOf(lop.id in userLopIds) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isChecked = !isChecked
                                onConfirm(userWithLops.user, lop, isChecked)
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { newCheckedState ->
                                isChecked = newCheckedState
                                onConfirm(userWithLops.user, lop, newCheckedState)
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
                Text("Done")
            }
        }
    )
}
