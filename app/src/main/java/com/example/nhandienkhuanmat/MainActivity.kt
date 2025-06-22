package com.example.nhandienkhuanmat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nhandienkhuanmat.presentation.screens.AttendanceScreen
import com.example.nhandienkhuanmat.presentation.screens.LoginScreen
import com.example.nhandienkhuanmat.presentation.screens.RegisterScreen
import com.example.nhandienkhuanmat.presentation.screens.ClassManagementScreen
import com.example.nhandienkhuanmat.presentation.screens.ClassDetailScreen
import com.example.nhandienkhuanmat.presentation.screens.UserManagementScreen
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
                FaceAttendanceNavHost()
            }
        }
    }
}

@Composable
fun FaceAttendanceNavHost() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val currentUser by mainViewModel.currentUser.collectAsState()
    val userWithLops by mainViewModel.userWithLops.collectAsState()

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
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                viewModel = mainViewModel
            )
        }
        composable("register") {
            RegisterScreen(
                onRegister = { name, email ->
                    mainViewModel.register(name, email)
                    // Navigate back to login after registration
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("main") {
            MainScreen(
                currentUser = currentUser,
                userWithLops = userWithLops,
                onLogout = {
                    mainViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToClassManagement = {
                    navController.navigate("class_management")
                },
                onNavigateToUserManagement = {
                    navController.navigate("user_management")
                },
                onNavigateToAttendance = { lopId ->
                    navController.navigate("attendance/$lopId")
                }
            )
        }
        composable("class_management") {
            ClassManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToClassDetail = { lopId ->
                    navController.navigate("class_detail/$lopId")
                }
            )
        }
        composable("user_management") {
            UserManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            "attendance/{lopId}",
            arguments = listOf(navArgument("lopId") { type = NavType.LongType })
        ) { backStackEntry ->
            val lopId = backStackEntry.arguments?.getLong("lopId")
            if (lopId != null) {
                AttendanceScreen(lopId = lopId)
            } else {
                // Handle error, maybe navigate back
                navController.popBackStack()
            }
        }
        composable(
            "class_detail/{lopId}",
            arguments = listOf(navArgument("lopId") { type = NavType.LongType })
        ) { backStackEntry ->
            val lopId = backStackEntry.arguments?.getLong("lopId")
            if (lopId != null) {
                ClassDetailScreen(
                    lopId = lopId,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun MainScreen(
    currentUser: com.example.nhandienkhuanmat.data.model.User?,
    userWithLops: com.example.nhandienkhuanmat.data.model.UserWithLops?,
    onLogout: () -> Unit,
    onNavigateToClassManagement: () -> Unit,
    onNavigateToUserManagement: () -> Unit,
    onNavigateToAttendance: (Long) -> Unit
) {
    if (currentUser == null) {
        // Show a loading screen while user data is being fetched
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (currentUser.role == com.example.nhandienkhuanmat.data.model.UserRole.ADMIN) {
        AdminDashboard(
            onLogout = onLogout,
            onNavigateToClassManagement = onNavigateToClassManagement,
            onNavigateToUserManagement = onNavigateToUserManagement
        )
    } else {
        UserDashboard(
            currentUser = currentUser,
            userWithLops = userWithLops,
            onLogout = onLogout,
            onNavigateToAttendance = onNavigateToAttendance
        )
    }
}

@Composable
fun AdminDashboard(
    onLogout: () -> Unit,
    onNavigateToClassManagement: () -> Unit,
    onNavigateToUserManagement: () -> Unit
) {
    // Placeholder for the Admin Dashboard
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Admin Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToClassManagement) {
            Text("Quản lý lớp học")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToUserManagement) {
            Text("Quản lý người dùng")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLogout) {
            Text("Đăng xuất")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(
    currentUser: com.example.nhandienkhuanmat.data.model.User?,
    userWithLops: com.example.nhandienkhuanmat.data.model.UserWithLops?,
    onLogout: () -> Unit,
    onNavigateToAttendance: (Long) -> Unit
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
                if (userWithLops != null && userWithLops.lops.isNotEmpty()) {
                    ClassSelectionScreen(
                        modifier = Modifier.padding(innerPadding),
                        classes = userWithLops.lops,
                        onClassSelected = { lopId ->
                            onNavigateToAttendance(lopId)
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Bạn chưa được gán vào lớp học nào.")
                    }
                }
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

@Composable
fun ClassSelectionScreen(
    modifier: Modifier = Modifier,
    classes: List<com.example.nhandienkhuanmat.data.model.Lop>,
    onClassSelected: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Chọn lớp học để điểm danh", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(classes) { lop ->
                Button(
                    onClick = { onClassSelected(lop.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(lop.name)
                }
            }
        }
    }
}