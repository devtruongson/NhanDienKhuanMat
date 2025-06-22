package com.example.nhandienkhuanmat.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nhandienkhuanmat.data.model.Lop
import com.example.nhandienkhuanmat.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassManagementScreen(
    onNavigateBack: () -> Unit,
    onNavigateToClassDetail: (Long) -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val lops by viewModel.lops.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedLop by remember { mutableStateOf<Lop?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý lớp học") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedLop = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Class")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(lops) { lop ->
                LopItem(
                    lop = lop,
                    onEdit = {
                        selectedLop = it
                        showDialog = true
                    },
                    onDelete = {
                        viewModel.deleteLop(it)
                    },
                    onClick = {
                        onNavigateToClassDetail(lop.id)
                    }
                )
                HorizontalDivider()
            }
        }

        if (showDialog) {
            LopEditDialog(
                lop = selectedLop,
                onDismiss = { showDialog = false },
                onConfirm = { name, description ->
                    if (selectedLop == null) {
                        viewModel.createLop(name, description)
                    } else {
                        viewModel.updateLop(selectedLop!!.copy(name = name, description = description))
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun LopItem(
    lop: Lop,
    onEdit: (Lop) -> Unit,
    onDelete: (Lop) -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = lop.name, style = MaterialTheme.typography.titleMedium)
            Text(text = lop.description ?: "", style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = { onEdit(lop) }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = { onDelete(lop) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@Composable
fun LopEditDialog(
    lop: Lop?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember(lop) { mutableStateOf(lop?.name ?: "") }
    var description by remember(lop) { mutableStateOf(lop?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (lop == null) "Tạo lớp mới" else "Chỉnh sửa lớp") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên lớp") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, description)
                    }
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
} 