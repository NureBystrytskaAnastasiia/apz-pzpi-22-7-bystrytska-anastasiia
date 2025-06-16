package com.example.mobile.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile.ui.theme.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacistScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    viewModel: MedicineViewModel = viewModel(),
    onOrdersClick: () -> Unit,
    onRoomsClick: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<PharmacistScreenType>(PharmacistScreenType.STATS) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Панель фармацевта",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Filled.Logout,
                            contentDescription = "Вийти",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Analytics, contentDescription = "Статистика") },
                    label = { Text("Статистика") },
                    selected = currentScreen == PharmacistScreenType.STATS,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onClick = { currentScreen = PharmacistScreenType.STATS }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Medication, contentDescription = "Ліки") },
                    label = { Text("Ліки") },
                    selected = currentScreen == PharmacistScreenType.MEDICINES,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onClick = { currentScreen = PharmacistScreenType.MEDICINES }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Замовлення") },
                    label = { Text("Замовлення") },
                    selected = currentScreen == PharmacistScreenType.ORDERS,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onClick = {
                        currentScreen = PharmacistScreenType.ORDERS
                        onOrdersClick()
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Кімнати") },
                    label = { Text("Кімнати") },
                    selected = currentScreen == PharmacistScreenType.ROOMS,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onClick = {
                        currentScreen = PharmacistScreenType.ROOMS
                        onRoomsClick()
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                PharmacistScreenType.STATS -> PharmacistStatsView(viewModel)
                PharmacistScreenType.MEDICINES -> MedicineListScreen(
                    onBack = {},
                    viewModel = viewModel,
                    isAdmin = false
                )
                PharmacistScreenType.ORDERS -> {}
                PharmacistScreenType.ROOMS -> {}
            }
        }
    }
}

@Composable
fun PharmacistStatsView(viewModel: MedicineViewModel) {
    val stats by viewModel.medicineStats.collectAsState()
    val storageIssues by viewModel.storageIssues.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getMedicineStats()
        viewModel.getMedicinesWithStorageIssues()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        errorMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { viewModel.clearErrorMessage() },
                title = { Text("Помилка", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium) },
                text = { Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearErrorMessage() }) {
                        Text("OK", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            stats?.let { StatsView(stats = it, storageIssues = storageIssues) }
        }
    }
}

private enum class PharmacistScreenType {
    STATS, MEDICINES, ORDERS, ROOMS
}
