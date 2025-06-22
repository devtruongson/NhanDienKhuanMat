package com.example.nhandienkhuanmat.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nhandienkhuanmat.data.model.Lop
import com.example.nhandienkhuanmat.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStatisticsDashboard(
    onNavigateBack: () -> Unit,
    onNavigateToClassStatistics: (Long, String) -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val lops by viewModel.lops.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống kê theo lớp") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(lops) { lop ->
                ClassStatisticsItem(
                    lop = lop,
                    onClick = {
                        onNavigateToClassStatistics(lop.id, lop.name)
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ClassStatisticsItem(
    lop: Lop,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(lop.name) },
        supportingContent = { Text(lop.description ?: "") },
        modifier = Modifier.clickable(onClick = onClick).padding(vertical = 8.dp)
    )
} 