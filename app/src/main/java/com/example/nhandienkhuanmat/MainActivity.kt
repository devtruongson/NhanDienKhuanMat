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
import androidx.navigation.NavController
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
import com.example.nhandienkhuanmat.presentation.screens.FaceRegistrationScreen
import com.example.nhandienkhuanmat.presentation.screens.AdminStatisticsDashboard
import com.example.nhandienkhuanmat.presentation.screens.ClassStatisticsScreen
import com.example.nhandienkhuanmat.presentation.screens.UserStatisticsScreen
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
                onRegister = { name, email, password ->
                    mainViewModel.register(name, email, password)
                    // Navigate back to login after registration
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Main App Navigation
        composable("main") {
            MainScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable("class_management") {
            ClassManagementScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToClassDetail = { classId ->
                    navController.navigate("class_detail/$classId")
                }
            )
        }
        composable("user_management") {
            UserManagementScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFaceRegistration = { userId ->
                    navController.navigate("face_registration/$userId")
                }
            )
        }
        composable(
            "face_registration/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId")
            if (userId != null) {
                FaceRegistrationScreen(
                    userId = userId,
                    navController = navController,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        composable(
            "attendance/{lopId}",
            arguments = listOf(navArgument("lopId") { type = NavType.LongType })
        ) { backStackEntry ->
            val lopId = backStackEntry.arguments?.getLong("lopId")
            if (lopId != null) {
                AttendanceScreen(
                    lopId = lopId,
                    onAttendanceSuccess = { navController.popBackStack() }
                )
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
        composable("admin_statistics_dashboard") {
            AdminStatisticsDashboard(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToClassStatistics = { lopId, lopName ->
                    navController.navigate("class_statistics/$lopId/$lopName")
                }
            )
        }
        composable(
            "class_statistics/{lopId}/{lopName}",
            arguments = listOf(
                navArgument("lopId") { type = NavType.LongType },
                navArgument("lopName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lopId = backStackEntry.arguments?.getLong("lopId")
            val lopName = backStackEntry.arguments?.getString("lopName")
            if (lopId != null && lopName != null) {
                ClassStatisticsScreen(
                    lopId = lopId,
                    lopName = lopName,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val currentUser by mainViewModel.currentUser.collectAsState()
    val userWithLops by mainViewModel.userWithLops.collectAsState()

    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (currentUser!!.role == com.example.nhandienkhuanmat.data.model.UserRole.ADMIN) {
        AdminDashboard(
            onLogout = {
                mainViewModel.logout()
                navController.navigate("login") { popUpTo(0) { inclusive = true } }
            },
            onNavigateToClassManagement = { navController.navigate("class_management") },
            onNavigateToUserManagement = { navController.navigate("user_management") },
            onNavigateToStatistics = { navController.navigate("admin_statistics_dashboard") }
        )
    } else {
        UserDashboard(
            currentUser = currentUser,
            userWithLops = userWithLops,
            onLogout = {
                mainViewModel.logout()
                navController.navigate("login") { popUpTo(0) { inclusive = true } }
            },
            onNavigateToAttendance = { lopId ->
                navController.navigate("attendance/$lopId")
            },
            mainViewModel = mainViewModel
        )
    }
}

@Composable
fun AdminDashboard(
    onLogout: () -> Unit,
    onNavigateToClassManagement: () -> Unit,
    onNavigateToUserManagement: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
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
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToStatistics) {
            Text("Thống kê điểm danh")
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
    onNavigateToAttendance: (Long) -> Unit,
    mainViewModel: MainViewModel
) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Điểm danh", "Thống kê", "Cài đặt")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Điểm danh khuôn mặt") },
                actions = {
                    Text(text = currentUser?.name ?: "User", modifier = Modifier.padding(end = 8.dp))
                    IconButton(onClick = onLogout) {
                        Text("Đăng xuất")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { /* TODO: Add icons */ },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedItem == 0) { // Show FAB only on the attendance tab
                FloatingActionButton(onClick = {
                    userWithLops?.lops?.firstOrNull()?.let {
                        onNavigateToAttendance(it.id)
                    }
                }) {
                    Text("+")
                }
            }
        }
    ) { innerPadding ->
        when (selectedItem) {
            0 -> ClassListForUser(
                userWithLops = userWithLops,
                modifier = Modifier.padding(innerPadding),
                onClassSelected = onNavigateToAttendance
            )
            1 -> UserStatisticsScreen(mainViewModel = mainViewModel)
            2 -> SettingsScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun ClassListForUser(
    userWithLops: com.example.nhandienkhuanmat.data.model.UserWithLops?,
    modifier: Modifier = Modifier,
    onClassSelected: (Long) -> Unit
) {
    if (userWithLops != null && userWithLops.lops.isNotEmpty()) {
        LazyColumn(modifier = modifier.padding(16.dp)) {
            items(userWithLops.lops) { lop ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { onClassSelected(lop.id) }
                ) {
                    Text(
                        text = lop.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Bạn chưa được gán vào lớp học nào.")
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Màn hình cài đặt")
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