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
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush

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
        modifier = modifier.background(SurfaceLight),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Панель фармацевта",
                        style = MaterialTheme.typography.headlineSmall, // Changed from AppTypography
                        color = TextOnPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AccentPrimary
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Filled.Logout,
                            contentDescription = "Вийти",
                            tint = TextOnPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceVariant
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Filled.Analytics,
                            contentDescription = "Статистика",
                            tint = if (currentScreen == PharmacistScreenType.STATS) {
                                AccentPrimary
                            } else {
                                Outline
                            }
                        )
                    },
                    label = {
                        Text(
                            "Статистика",
                            style = MaterialTheme.typography.labelMedium, // Changed from AppTypography
                            color = if (currentScreen == PharmacistScreenType.STATS) {
                                AccentPrimary
                            } else {
                                Outline
                            }
                        )
                    },
                    selected = currentScreen == PharmacistScreenType.STATS,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentPrimary,
                        selectedTextColor = AccentPrimary,
                        unselectedIconColor = Outline,
                        unselectedTextColor = Outline,
                        indicatorColor = SurfaceLight
                    ),
                    onClick = { currentScreen = PharmacistScreenType.STATS }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Filled.Medication,
                            contentDescription = "Ліки",
                            tint = if (currentScreen == PharmacistScreenType.MEDICINES) {
                                AccentPrimary
                            } else {
                                Outline
                            }
                        )
                    },
                    label = {
                        Text(
                            "Ліки",
                            style = MaterialTheme.typography.labelMedium, // Changed from AppTypography
                            color = if (currentScreen == PharmacistScreenType.MEDICINES) {
                                AccentPrimary
                            } else {
                                Outline
                            }
                        )
                    },
                    selected = currentScreen == PharmacistScreenType.MEDICINES,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentPrimary,
                        selectedTextColor = AccentPrimary,
                        unselectedIconColor = Outline,
                        unselectedTextColor = Outline,
                        indicatorColor = SurfaceLight
                    ),
                    onClick = { currentScreen = PharmacistScreenType.MEDICINES }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = "Замовлення",
                            tint = if (currentScreen == PharmacistScreenType.ORDERS) {
                                AccentPrimary
                            } else {
                                Outline
                            }
                        )
                    },
                    label = {
                        Text(
                            "Замовлення",
                            style = MaterialTheme.typography.labelMedium, // Changed from AppTypography
                            color = if (currentScreen == PharmacistScreenType.ORDERS) {
                                AccentPrimary
                            } else {
                                Outline
                            }
                        )
                    },
                    selected = currentScreen == PharmacistScreenType.ORDERS,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentPrimary,
                        selectedTextColor = AccentPrimary,
                        unselectedIconColor = Outline,
                        unselectedTextColor = Outline,
                        indicatorColor = SurfaceLight
                    ),
                    onClick = {
                        currentScreen = PharmacistScreenType.ORDERS
                        onOrdersClick()
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Кімнати",
                            tint = if (currentScreen == PharmacistScreenType.ROOMS) {
                                AccentPrimary
                            } else {
                                Outline
                            }
                        )
                    },
                    label = {
                        Text(
                            "Кімнати",
                            style = MaterialTheme.typography.labelMedium, // Changed from AppTypography
                            color = if (currentScreen == PharmacistScreenType.ROOMS) {
                                AccentPrimary
                            } else {
                                Outline
                            }
                        )
                    },
                    selected = currentScreen == PharmacistScreenType.ROOMS,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentPrimary,
                        selectedTextColor = AccentPrimary,
                        unselectedIconColor = Outline,
                        unselectedTextColor = Outline,
                        indicatorColor = SurfaceLight
                    ),
                    onClick = {
                        currentScreen = PharmacistScreenType.ROOMS
                        onRoomsClick()
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(SurfaceLight)
        ) {
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

    Column(modifier = Modifier.padding(AppDimensions.paddingLarge)) {
        errorMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { viewModel.clearErrorMessage() },
                title = {
                    Text(
                        "Помилка",
                        style = MaterialTheme.typography.titleMedium, // Changed from AppTypography
                        color = Error
                    )
                },
                text = {
                    Text(
                        message,
                        style = MaterialTheme.typography.bodyMedium, // Changed from AppTypography
                        color = TextOnSurface
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearErrorMessage() }) {
                        Text(
                            "OK",
                            style = MaterialTheme.typography.labelLarge, // Changed from AppTypography
                            color = AccentPrimary
                        )
                    }
                },
                shape = AppShapes.dialog
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentPrimary)
            }
        } else {
            stats?.let { StatsView(stats = it, storageIssues = storageIssues) }
        }
    }
}

private enum class PharmacistScreenType {
    STATS, MEDICINES, ORDERS, ROOMS
}
