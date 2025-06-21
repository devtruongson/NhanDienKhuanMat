package com.example.nhandienkhuanmat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nhandienkhuanmat.presentation.screens.AttendanceScreen
import com.example.nhandienkhuanmat.presentation.screens.LoginScreen
import com.example.nhandienkhuanmat.presentation.viewmodel.MainViewModel
import com.example.nhandienkhuanmat.ui.theme.NhanDienKhuanMatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NhanDienKhuanMatTheme {
                FaceAttendanceApp()
            }
        }
    }
}

@Composable
fun FaceAttendanceApp() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()
    val currentUser by mainViewModel.currentUser.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                currentUser = currentUser,
                onLogout = {
                    mainViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentUser: com.example.nhandienkhuanmat.data.model.User?,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Điểm danh", "Thống kê", "Cài đặt")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Điểm danh khuôn mặt") },
                actions = {
                    currentUser?.let { user ->
                        Text(
                            text = user.name,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                    IconButton(onClick = onLogout) {
                        // Logout icon
                        Text("Đăng xuất")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { /* Icon would go here */ },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> {
                AttendanceScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
            1 -> {
                // Statistics screen (placeholder)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Thống kê điểm danh",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tính năng đang phát triển...")
                }
            }
            2 -> {
                // Settings screen (placeholder)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Cài đặt",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tính năng đang phát triển...")
                }
            }
        }
    }
}