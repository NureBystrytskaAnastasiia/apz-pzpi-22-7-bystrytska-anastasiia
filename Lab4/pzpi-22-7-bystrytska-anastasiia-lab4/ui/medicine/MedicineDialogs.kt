package com.example.mobile.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mobile.data.model.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineDialog(
    onDismiss: () -> Unit,
    onConfirm: (MedicineRequest) -> Unit,
    viewModel: MedicineViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }
    var storageRoomId by remember { mutableStateOf("") }
    var tempMin by remember { mutableStateOf("") }
    var tempMax by remember { mutableStateOf("") }
    var humMin by remember { mutableStateOf("") }
    var humMax by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val rooms by viewModel.rooms.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllRooms()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Додати нові ліки") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Назва") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Кількість") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Ціна") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expirationDate,
                    onValueChange = { expirationDate = it },
                    label = { Text("Термін придатності (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    label = { Text("Постачальник") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            value = rooms.find { it._id == storageRoomId }?.name ?: "Оберіть кімнату",
                            onValueChange = { },
                            label = { Text("Кімната зберігання") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            rooms.forEach { room ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${room.name} (${room.temperature}°C, ${room.humidity}%)")
                                    },
                                    onClick = {
                                        storageRoomId = room._id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Температурний діапазон:", style = MaterialTheme.typography.labelMedium)
                Row {
                    OutlinedTextField(
                        value = tempMin,
                        onValueChange = { tempMin = it },
                        label = { Text("Мін") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = tempMax,
                        onValueChange = { tempMax = it },
                        label = { Text("Макс") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Діапазон вологості:", style = MaterialTheme.typography.labelMedium)
                Row {
                    OutlinedTextField(
                        value = humMin,
                        onValueChange = { humMin = it },
                        label = { Text("Мін") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = humMax,
                        onValueChange = { humMax = it },
                        label = { Text("Макс") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Інструкції зберігання") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        MedicineRequest(
                            name = name,
                            quantity = quantity.toIntOrNull() ?: 0,
                            expirationDate = expirationDate,
                            supplier = supplier,
                            price = price.toDoubleOrNull() ?: 0.0,
                            storageRoomId = storageRoomId,
                            temperatureRange = TemperatureRange(
                                min = tempMin.toDoubleOrNull() ?: 0.0,
                                max = tempMax.toDoubleOrNull() ?: 0.0
                            ),
                            humidityRange = HumidityRange(
                                min = humMin.toDoubleOrNull() ?: 0.0,
                                max = humMax.toDoubleOrNull() ?: 0.0
                            ),
                            storageInstructions = instructions.ifEmpty { null }
                        )
                    )
                },
                enabled = name.isNotBlank() && quantity.isNotBlank() && price.isNotBlank() && storageRoomId.isNotBlank()
            ) {
                Text("Додати")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicineDialog(
    medicine: MedicineResponse,
    onDismiss: () -> Unit,
    onConfirm: (MedicineRequest) -> Unit,
    viewModel: MedicineViewModel = viewModel()
) {
    var name by remember { mutableStateOf(medicine.name) }
    var quantity by remember { mutableStateOf(medicine.quantity.toString()) }
    var price by remember { mutableStateOf(medicine.price.toString()) }
    var expirationDate by remember { mutableStateOf(medicine.expirationDate) }
    var supplier by remember { mutableStateOf(medicine.supplier) }
    var storageRoomId by remember { mutableStateOf(medicine.storageRoomId._id) }
    var tempMin by remember { mutableStateOf(medicine.temperatureRange.min.toString()) }
    var tempMax by remember { mutableStateOf(medicine.temperatureRange.max.toString()) }
    var humMin by remember { mutableStateOf(medicine.humidityRange.min.toString()) }
    var humMax by remember { mutableStateOf(medicine.humidityRange.max.toString()) }
    var instructions by remember { mutableStateOf(medicine.storageInstructions ?: "") }
    var expanded by remember { mutableStateOf(false) }
    val rooms by viewModel.rooms.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllRooms()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редагувати ліки") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Назва") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Кількість") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Ціна") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expirationDate,
                    onValueChange = { expirationDate = it },
                    label = { Text("Термін придатності") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    label = { Text("Постачальник") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            value = rooms.find { it._id == storageRoomId }?.name ?: "Оберіть кімнату",
                            onValueChange = { },
                            label = { Text("Кімната зберігання") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            rooms.forEach { room ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${room.name} (${room.temperature}°C, ${room.humidity}%)")
                                    },
                                    onClick = {
                                        storageRoomId = room._id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Температурний діапазон:", style = MaterialTheme.typography.labelMedium)
                Row {
                    OutlinedTextField(
                        value = tempMin,
                        onValueChange = { tempMin = it },
                        label = { Text("Мін") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = tempMax,
                        onValueChange = { tempMax = it },
                        label = { Text("Макс") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Діапазон вологості:", style = MaterialTheme.typography.labelMedium)
                Row {
                    OutlinedTextField(
                        value = humMin,
                        onValueChange = { humMin = it },
                        label = { Text("Мін") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = humMax,
                        onValueChange = { humMax = it },
                        label = { Text("Макс") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Інструкції зберігання") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        MedicineRequest(
                            name = name,
                            quantity = quantity.toIntOrNull() ?: 0,
                            expirationDate = expirationDate,
                            supplier = supplier,
                            price = price.toDoubleOrNull() ?: 0.0,
                            storageRoomId = storageRoomId,
                            temperatureRange = TemperatureRange(
                                min = tempMin.toDoubleOrNull() ?: 0.0,
                                max = tempMax.toDoubleOrNull() ?: 0.0
                            ),
                            humidityRange = HumidityRange(
                                min = humMin.toDoubleOrNull() ?: 0.0,
                                max = humMax.toDoubleOrNull() ?: 0.0
                            ),
                            storageInstructions = instructions.ifEmpty { null }
                        )
                    )
                },
                enabled = name.isNotBlank() && quantity.isNotBlank() && price.isNotBlank() && storageRoomId.isNotBlank()
            ) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}
