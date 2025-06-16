package com.example.mobile.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile.data.model.MedicineResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineListScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: MedicineViewModel = viewModel(),
    isAdmin: Boolean = false
) {
    val medicines by viewModel.medicines.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<MedicineResponse?>(null) }

    // Load medicines on first launch
    LaunchedEffect(Unit) {
        viewModel.getAllMedicines()
    }

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Управління ліками",
                        color = colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.primary,
                    titleContentColor = colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.getAllMedicines() }) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "Оновити",
                            tint = colorScheme.onPrimary
                        )
                    }
                    if (isAdmin) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Додати ліки",
                                tint = colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        },
        containerColor = colorScheme.background
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Error message
            errorMessage?.let { message ->
                AlertDialog(
                    onDismissRequest = { viewModel.clearErrorMessage() },
                    title = {
                        Text(
                            "Помилка",
                            color = colorScheme.onBackground
                        )
                    },
                    text = {
                        Text(
                            message,
                            color = colorScheme.onSurfaceVariant
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.clearErrorMessage() },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colorScheme.primary
                            )
                        ) {
                            Text("OK")
                        }
                    },
                    containerColor = colorScheme.background
                )
            }

            // Loading indicator
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            } else {
                // Medicine list
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(medicines) { medicine ->
                        MedicineItem(
                            medicine = medicine,
                            onEdit = {
                                selectedMedicine = medicine
                                showEditDialog = true
                            },
                            onDelete = { if (isAdmin) viewModel.deleteMedicine(medicine._id) },
                            isAdmin = isAdmin
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // Діалог додавання ліків
    if (showAddDialog) {
        AddMedicineDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { medicine ->
                viewModel.createMedicine(medicine)
                showAddDialog = false
            },
            viewModel = viewModel
        )
    }

    // Діалог редагування ліків
    if (showEditDialog && selectedMedicine != null) {
        EditMedicineDialog(
            medicine = selectedMedicine!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { medicine ->
                viewModel.updateMedicine(selectedMedicine!!._id, medicine)
                showEditDialog = false
            },
            viewModel = viewModel
        )
    }
}

@Composable
fun MedicineItem(
    medicine: MedicineResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isAdmin: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = CardDefaults.outlinedCardBorder(true)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                medicine.name,
                style = typography.headlineSmall,
                color = colorScheme.onSurface
            )
            Text(
                "Кількість: ${medicine.quantity}",
                color = colorScheme.onSurfaceVariant
            )
            Text(
                "Ціна: ${medicine.price} грн",
                color = colorScheme.onSurfaceVariant
            )
            Text(
                "Термін придатності: ${medicine.expirationDate}",
                color = colorScheme.onSurfaceVariant
            )
            Text(
                "Постачальник: ${medicine.supplier}",
                color = colorScheme.onSurfaceVariant
            )
            Text(
                "Кімната зберігання: ${medicine.storageRoomId.name}",
                color = colorScheme.onSurfaceVariant
            )
            Text(
                "Статус зберігання: ${medicine.storageStatus}",
                color = if (medicine.storageStatus == "Оптимальні умови") colorScheme.primary else colorScheme.error
            )

            if (medicine.isExpiringSoon) {
                Text(
                    "❗ Закінчується термін придатності",
                    color = colorScheme.error,
                    style = typography.bodyMedium
                )
            }

            if (isAdmin) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Редагувати",
                            tint = colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Видалити",
                            tint = colorScheme.error
                        )
                    }
                }
            }
        }
    }
}