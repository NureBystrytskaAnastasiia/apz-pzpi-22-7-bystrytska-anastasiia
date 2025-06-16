package com.example.mobile.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile.ui.theme.AppDimensions
import com.example.mobile.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    onBack: () -> Unit,
    viewModel: OrderViewModel = viewModel()
) {
    val context = LocalContext.current
    val order by viewModel.selectedOrder.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.getOrderById(orderId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Деталі замовлення",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                    .padding(AppDimensions.paddingLarge)
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
                } else if (order != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AppDimensions.paddingMedium),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
                    ) {
                        item {
                            Column {
                                Text(
                                    text = "Замовлення #${order!!._id}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))
                                Text(
                                    text = "Статус: ${getStatusText(order!!.status)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Загальна сума: ${order!!.totalAmount} грн",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Дата створення: ${order!!.createdAt}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (order!!.deliveredAt != null) {
                                    Text(
                                        text = "Дата доставки: ${order!!.deliveredAt}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (order!!.completedAt != null) {
                                    Text(
                                        text = "Дата завершення: ${order!!.completedAt}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (order!!.invoiceNumber != null) {
                                    Text(
                                        text = "Накладна: ${order!!.invoiceNumber}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))
                                Text(
                                    text = "Товари:",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        items(order!!.items) { item ->
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
                                        text = item.medicineName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                                    Text(
                                        text = "Постачальник: ${item.supplier}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Кількість: ${item.quantity}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Ціна за одиницю: ${item.unitPrice} грн",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Сума: ${item.totalPrice} грн",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        item {
                            if (order!!.status == "delivered") {
                                Button(
                                    onClick = { viewModel.completeOrder(order!!._id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = AppDimensions.paddingLarge),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(
                                        text = "Завершити замовлення",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
