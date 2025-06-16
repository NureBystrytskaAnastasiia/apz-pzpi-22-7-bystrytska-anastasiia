package com.example.mobile.ui.room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile.data.model.Room
import com.example.mobile.ui.theme.AppDimensions
import com.example.mobile.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    onBack: () -> Unit,
    viewModel: RoomViewModel = viewModel()
) {
    val rooms by viewModel.rooms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRooms()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Моніторинг кімнат",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.fetchRooms() },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Оновити",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppDimensions.paddingMedium)
            ) {
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
                } else if (rooms.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Кімнати не знайдено",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(AppDimensions.paddingMedium),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
                    ) {
                        items(rooms) { room ->
                            RoomCard(room = room)
                        }
                    }
                }

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
            }
        }
    }
}

@Composable
fun RoomCard(room: Room) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(AppDimensions.paddingMedium)
        ) {
            Text(
                text = room.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            room.description?.let { description ->
                if (description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SensorDataItem(
                    label = "Температура",
                    value = room.temperature?.let { "${it}°C" } ?: "---",
                    isWarning = room.temperature?.let { it < -10 || it > 25 } ?: false
                )

                SensorDataItem(
                    label = "Вологість",
                    value = room.humidity?.let { "${it}%" } ?: "---",
                    isWarning = room.humidity?.let { it < 30 || it > 80 } ?: false
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            Text(
                text = "Оновлено: ${room.updatedAt ?: "Невідомо"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SensorDataItem(label: String, value: String, isWarning: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isWarning) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )
    }
}
