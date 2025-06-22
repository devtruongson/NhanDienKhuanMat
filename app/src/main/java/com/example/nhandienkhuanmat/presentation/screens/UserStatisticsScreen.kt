package com.example.nhandienkhuanmat.presentation.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nhandienkhuanmat.presentation.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

data class UserDailySummary(
    val date: String,
    val lopName: String,
    val firstCheckIn: Long,
    val lastCheckOut: Long?
)

@Composable
fun UserStatisticsScreen(mainViewModel: MainViewModel) {
    val attendanceHistory by mainViewModel.userAttendanceHistory.collectAsState()
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        mainViewModel.loadUserAttendanceHistory()
    }

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

    val filteredList = if (selectedDate == null) {
        attendanceHistory
    } else {
        attendanceHistory.filter {
            val attendanceDate = Calendar.getInstance().apply { timeInMillis = it.attendance.checkInTime }
            attendanceDate.get(Calendar.YEAR) == selectedDate!!.get(Calendar.YEAR) &&
                    attendanceDate.get(Calendar.DAY_OF_YEAR) == selectedDate!!.get(Calendar.DAY_OF_YEAR)
        }
    }

    val dailySummaries = filteredList
        .groupBy {
            val cal = Calendar.getInstance().apply { timeInMillis = it.attendance.checkInTime }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
        .map { (_, records) ->
            val firstRecord = records.first()
            val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(firstRecord.attendance.checkInTime))
            val firstCheckIn = records.minOf { it.attendance.checkInTime }
            val lastCheckOut = records.mapNotNull { it.attendance.checkOutTime }.maxOfOrNull { it }
            val lopName = firstRecord.lop.name
            UserDailySummary(dateStr, lopName, firstCheckIn, lastCheckOut)
        }.sortedByDescending { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.date) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Lịch sử điểm danh", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Button(onClick = { showDatePicker = true }) {
                Text(
                    text = selectedDate?.let {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.time)
                    } ?: "Chọn ngày"
                )
            }
        }

        if (selectedDate != null) {
            TextButton(onClick = { selectedDate = null }) {
                Text("Xoá bộ lọc")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (dailySummaries.isEmpty()) {
            Text(
                text = if (selectedDate != null) "Không có dữ liệu cho ngày được chọn." else "Không có lịch sử điểm danh.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dailySummaries) { summary ->
                    AttendanceSummaryItem(summary = summary)
                }
            }
        }
    }
}

@Composable
fun AttendanceSummaryItem(summary: UserDailySummary) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Ngày: ${summary.date}", fontWeight = FontWeight.Bold)
            Text("Lớp: ${summary.lopName}")
            Text(
                "Check-in: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(summary.firstCheckIn))}"
            )
            summary.lastCheckOut?.let {
                Text(
                    "Check-out: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(it))}"
                )
            } ?: Text("Check-out: Chưa có")
        }
    }
}
