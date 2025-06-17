package com.example.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobile.data.storage.TokenStorage
import com.example.mobile.ui.login.AuthScreen
import com.example.mobile.ui.login.LoginViewModel
import com.example.mobile.ui.login.LoginViewModelFactory
import com.example.mobile.ui.medicine.AdminScreen
import com.example.mobile.ui.medicine.MedicineViewModel
import com.example.mobile.ui.medicine.PharmacistScreen
import com.example.mobile.ui.order.OrderDetailScreen
import com.example.mobile.ui.order.OrderListScreen
import com.example.mobile.ui.order.OrderViewModel
import com.example.mobile.ui.room.RoomListScreen
import com.example.mobile.ui.user.UserListScreen
import com.example.mobile.ui.theme.MobileTheme
import kotlinx.coroutines.launch
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTheme(dynamicColor = false){
                val tokenStorage = TokenStorage(this)

                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(tokenStorage)
                )
                val medicineViewModel: MedicineViewModel = viewModel()
                val orderViewModel: OrderViewModel = viewModel()

                val navController = rememberNavController()
                var isLoggedIn by remember { mutableStateOf(false) }
                var userRole by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    launch {
                        tokenStorage.tokenFlow.collect { token ->
                            val wasLoggedIn = isLoggedIn
                            isLoggedIn = !token.isNullOrBlank()

                            Log.d("MainActivity", "Token changed: isLoggedIn = $isLoggedIn")

                            if (!isLoggedIn) {
                                userRole = null
                                Log.d("MainActivity", "User logged out, clearing role")
                            }

                            // Якщо користувач вийшов з системи, переходимо на екран авторизації
                            if (wasLoggedIn && !isLoggedIn) {
                                navController.navigate("auth") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                    launch {
                        tokenStorage.roleFlow.collect { role ->
                            Log.d("MainActivity", "Role changed: $role")
                            userRole = role
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) "main" else "auth"
                ) {
                    composable("auth") {
                        AuthScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = { role ->
                                Log.d("MainActivity", "Login success with role: $role")
                                userRole = role
                                navController.navigate("main") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main") {
                        // Додаємо перевірку на роль та обробку помилок
                        when {
                            userRole == null -> {
                                // Показуємо екран завантаження або повертаємося до авторизації
                                LaunchedEffect(Unit) {
                                    Log.w("MainActivity", "User role is null, redirecting to auth")
                                    navController.navigate("auth") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            }
                            userRole == "admin" -> {
                                AdminScreen(
                                    onLogout = {
                                        Log.d("MainActivity", "Admin logout")
                                        loginViewModel.logout()
                                        navController.navigate("auth") {
                                            popUpTo("main") { inclusive = true }
                                        }
                                    },
                                    viewModel = medicineViewModel,
                                    onOrdersClick = { navController.navigate("orders") },
                                    onRoomsClick = { navController.navigate("rooms") },
                                    onUsersClick = { navController.navigate("users") }
                                )
                            }
                            userRole == "pharmacist" -> {
                                PharmacistScreen(
                                    onLogout = {
                                        Log.d("MainActivity", "Pharmacist logout")
                                        loginViewModel.logout()
                                        navController.navigate("auth") {
                                            popUpTo("main") { inclusive = true }
                                        }
                                    },
                                    viewModel = medicineViewModel,
                                    onOrdersClick = { navController.navigate("orders") },
                                    onRoomsClick = { navController.navigate("rooms") }
                                )
                            }
                            else -> {
                                // Невідома роль
                                LaunchedEffect(Unit) {
                                    Log.w("MainActivity", "Unknown user role: $userRole, logging out")
                                    loginViewModel.logout()
                                    navController.navigate("auth") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            }
                        }
                    }

                    composable("orders") {
                        OrderListScreen(
                            onBack = { navController.popBackStack() },
                            onOrderClick = { orderId -> navController.navigate("order/$orderId") },
                            viewModel = orderViewModel
                        )
                    }

                    composable("rooms") {
                        RoomListScreen(
                            onBack = { navController.popBackStack() },
                            viewModel = viewModel()
                        )
                    }

                    composable("users") {
                        UserListScreen(
                            onBack = { navController.popBackStack() },
                            viewModel = viewModel()
                        )
                    }

                    composable(
                        "order/{orderId}",
                        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        OrderDetailScreen(
                            orderId = backStackEntry.arguments?.getString("orderId") ?: "",
                            onBack = { navController.popBackStack() },
                            viewModel = orderViewModel
                        )
                    }
                }
            }
        }
    }
}
