package com.example.mobile.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    viewModel: MedicineViewModel = viewModel(),
    onOrdersClick: () -> Unit,
    onRoomsClick: () -> Unit,
    onUsersClick: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<AdminScreenType>(AdminScreenType.STATS) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Адмін панель",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Вийти",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.height(AppDimensions.bottomNavHeight)
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Analytics,
                            contentDescription = "Статистика"
                        )
                    },
                    label = {
                        Text(
                            text = "Статистика",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = currentScreen == AdminScreenType.STATS,
                    onClick = { currentScreen = AdminScreenType.STATS }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Medication,
                            contentDescription = "Ліки"
                        )
                    },
                    label = {
                        Text(
                            text = "Ліки",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = currentScreen == AdminScreenType.MEDICINES,
                    onClick = { currentScreen = AdminScreenType.MEDICINES }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Замовлення"
                        )
                    },
                    label = {
                        Text(
                            text = "Замовлення",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = currentScreen == AdminScreenType.ORDERS,
                    onClick = {
                        currentScreen = AdminScreenType.ORDERS
                        onOrdersClick()
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Кімнати"
                        )
                    },
                    label = {
                        Text(
                            text = "Кімнати",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = currentScreen == AdminScreenType.ROOMS,
                    onClick = {
                        currentScreen = AdminScreenType.ROOMS
                        onRoomsClick()
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.People,
                            contentDescription = "Користувачі"
                        )
                    },
                    label = {
                        Text(
                            text = "Користувачі",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = currentScreen == AdminScreenType.USERS,
                    onClick = {
                        currentScreen = AdminScreenType.USERS
                        onUsersClick()
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (currentScreen) {
                AdminScreenType.STATS -> AdminStatsView(viewModel)
                AdminScreenType.MEDICINES -> MedicineListScreen(
                    onBack = {},
                    viewModel = viewModel,
                    isAdmin = true
                )
                AdminScreenType.ORDERS -> {} // Navigation handled via onOrdersClick
                AdminScreenType.ROOMS -> {} // Navigation handled via onRoomsClick
                AdminScreenType.USERS -> {} // Navigation handled via onUsersClick
            }
        }
    }
}

@Composable
fun AdminStatsView(viewModel: MedicineViewModel) {
    val stats by viewModel.medicineStats.collectAsState()
    val storageIssues by viewModel.storageIssues.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getMedicineStats()
        viewModel.getMedicinesWithStorageIssues()
    }

    Column(
        modifier = Modifier
            .padding(AppDimensions.paddingLarge)
            .fillMaxSize()
    ) {
        errorMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { viewModel.clearErrorMessage() },
                title = {
                    Text(
                        text = "Помилка",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.clearErrorMessage() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.error,
                textContentColor = MaterialTheme.colorScheme.onSurface
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = AppDimensions.paddingSmall
                )
            }
        } else {
            stats?.let { StatsView(stats = it, storageIssues = storageIssues) }
        }
    }
}

private enum class AdminScreenType {
    STATS, MEDICINES, ORDERS, ROOMS, USERS
}
