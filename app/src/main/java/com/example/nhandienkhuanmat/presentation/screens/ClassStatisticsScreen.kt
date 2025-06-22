package com.example.nhandienkhuanmat.presentation.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nhandienkhuanmat.data.model.AttendanceWithDetails
import com.example.nhandienkhuanmat.presentation.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassStatisticsScreen(
    lopId: Long,
    lopName: String,
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val attendanceDetails by viewModel.attendanceDetails.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                selectedDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { showDatePicker = false }
            show()
        }
    }

    LaunchedEffect(lopId) {
        viewModel.loadAttendanceDetails(lopId)
    }

    val filteredList = attendanceDetails
        .filter {
            it.user.name.contains(searchQuery, ignoreCase = true)
        }
        .filter {
            if (selectedDate == null) return@filter true
            val attendanceDate = Calendar.getInstance().apply { timeInMillis = it.attendance.checkInTime }
            attendanceDate.get(Calendar.YEAR) == selectedDate!!.get(Calendar.YEAR) &&
            attendanceDate.get(Calendar.DAY_OF_YEAR) == selectedDate!!.get(Calendar.DAY_OF_YEAR)
        }

    val dailyUserSummaries = filteredList
        .groupBy { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.attendance.checkInTime)) }
        .mapValues { (_, records) ->
            records.groupBy { it.user }
                .map { (user, userRecords) ->
                    val firstCheckIn = userRecords.minOf { it.attendance.checkInTime }
                    val lastCheckOut = userRecords.mapNotNull { it.attendance.checkOutTime }.maxOfOrNull { it }
                    AttendanceSummary(user, firstCheckIn, lastCheckOut)
                }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống kê: $lopName") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Tìm theo tên sinh viên") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { showDatePicker = true }) {
                    Text(text = selectedDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.time) } ?: "Chọn ngày")
                }
                if (selectedDate != null) {
                    TextButton(onClick = { selectedDate = null }) {
                        Text("Xóa bộ lọc ngày")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (dailyUserSummaries.isEmpty()) {
                    item {
                        Text("Không có dữ liệu điểm danh.")
                    }
                } else {
                    dailyUserSummaries.forEach { (date, summaries) ->
                        item {
                            Text(text = "Ngày: $date", style = MaterialTheme.typography.headlineSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(summaries) { summary ->
                            AttendanceRecordItem(summary)
                        }
                    }
                }
            }
        }
    }
}

data class AttendanceSummary(
    val user: com.example.nhandienkhuanmat.data.model.User,
    val firstCheckIn: Long,
    val lastCheckOut: Long?
)

@Composable
fun AttendanceRecordItem(summary: AttendanceSummary) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val checkInTime = timeFormat.format(Date(summary.firstCheckIn))
    val checkOutTime = summary.lastCheckOut?.let { timeFormat.format(Date(it)) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = summary.user.name, modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("Vào: $checkInTime", style = MaterialTheme.typography.bodyMedium)
                if (checkOutTime != null) {
                    Text("Ra: $checkOutTime", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
} 