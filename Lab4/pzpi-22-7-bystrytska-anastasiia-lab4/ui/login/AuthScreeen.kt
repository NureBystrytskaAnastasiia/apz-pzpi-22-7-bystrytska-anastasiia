package com.example.mobile.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: LoginViewModel, onLoginSuccess: (String) -> Unit) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("pharmacist") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPassword by remember { mutableStateOf(false) }

    val loginResult by viewModel.loginResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    // Перехід на головний екран після успішного входу
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            userRole?.let { role ->
                onLoginSuccess(role)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(AppDimensions.paddingXXLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Логотип або декоративний елемент
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (showForgotPassword) Icons.Default.Lock
                    else if (isLoginMode) Icons.Default.Person
                    else Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(AppDimensions.iconSizeLarge)
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.marginXXLarge))

            // Заголовок
            Text(
                text = when {
                    showForgotPassword -> "Відновлення паролю"
                    isLoginMode -> "Вхід в систему"
                    else -> "Створення акаунту"
                },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                text = when {
                    showForgotPassword -> "Введіть email для відновлення паролю"
                    isLoginMode -> "Введіть дані для входу"
                    else -> "Заповніть форму для реєстрації"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = AppDimensions.paddingSmall)
            )

            Spacer(modifier = Modifier.height(AppDimensions.marginXXXLarge))

            // Форма в картці
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.cardLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(AppDimensions.paddingXXLarge),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.marginLarge)
                ) {
                    if (showForgotPassword) {
                        // Екран відновлення паролю
                        StyledTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email адреса",
                            leadingIcon = Icons.Default.Email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        StyledButton(
                            onClick = { viewModel.forgotPassword(email) },
                            enabled = !isLoading && email.isNotBlank(),
                            isLoading = isLoading,
                            text = "Відновити пароль",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(
                                onClick = {
                                    showForgotPassword = false
                                    viewModel.clearMessage()
                                }
                            ) {
                                Text(
                                    text = "Назад до входу",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                    } else {
                        // Основна форма входу/реєстрації
                        if (!isLoginMode) {
                            StyledTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = "Повне ім'я",
                                leadingIcon = Icons.Default.Person
                            )

                            // Вибір ролі
                            Text(
                                text = "Оберіть роль:",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = AppDimensions.paddingSmall)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                RoleOption(
                                    selected = role == "pharmacist",
                                    onClick = { role = "pharmacist" },
                                    text = "Фармацевт"
                                )
                                RoleOption(
                                    selected = role == "admin",
                                    onClick = { role = "admin" },
                                    text = "Адміністратор"
                                )
                            }
                        }

                        StyledTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email адреса",
                            leadingIcon = Icons.Default.Email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        StyledTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Пароль",
                            leadingIcon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onPasswordVisibilityToggle = { passwordVisible = !passwordVisible }
                        )

                        StyledButton(
                            onClick = {
                                if (isLoginMode) {
                                    viewModel.login(email, password)
                                } else {
                                    viewModel.register(email, password, name, role)
                                }
                            },
                            enabled = !isLoading && email.isNotBlank() && password.isNotBlank() &&
                                    (isLoginMode || name.isNotBlank()),
                            isLoading = isLoading,
                            text = if (isLoginMode) "Увійти" else "Зареєструватися",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (!showForgotPassword) {
                Spacer(modifier = Modifier.height(AppDimensions.marginLarge))

                // Перемикання режимів
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            isLoginMode = !isLoginMode
                            viewModel.clearMessage()
                        }
                    ) {
                        Text(
                            text = if (isLoginMode) "Немає акаунту? Зареєструватися"
                            else "Вже є акаунт? Увійти",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Забув пароль (тільки в режимі входу)
                if (isLoginMode) {
                    TextButton(
                        onClick = {
                            showForgotPassword = true
                            viewModel.clearMessage()
                        }
                    ) {
                        Text(
                            text = "Забули пароль?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Повідомлення про результат
            loginResult?.let { message ->
                Spacer(modifier = Modifier.height(AppDimensions.marginLarge))

                StatusCard(
                    message = message,
                    isSuccess = message.contains("Успішн", ignoreCase = true)
                )
            }
        }
    }
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onPasswordVisibilityToggle?.invoke() }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Сховати пароль" else "Показати пароль",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        shape = AppShapes.textFieldMedium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(AppDimensions.textFieldHeightLarge)
    )
}

@Composable
private fun StyledButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = AppShapes.buttonMedium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        ),
        modifier = modifier.height(AppDimensions.buttonHeightLarge)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(AppDimensions.iconSizeMedium),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun RoleOption(
    selected: Boolean,
    onClick: () -> Unit,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = AppDimensions.paddingSmall)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = AppDimensions.paddingSmall)
        )
    }
}

@Composable
private fun StatusCard(
    message: String,
    isSuccess: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.cardSmall,
        colors = CardDefaults.cardColors(
            containerColor = if (isSuccess)
                MaterialTheme.extendedColors.successContainer
            else MaterialTheme.extendedColors.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(AppDimensions.paddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.Person else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isSuccess)
                    MaterialTheme.extendedColors.onSuccessContainer
                else MaterialTheme.extendedColors.onErrorContainer,
                modifier = Modifier.size(AppDimensions.iconSizeMedium)
            )

            Spacer(modifier = Modifier.width(AppDimensions.paddingMedium))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSuccess)
                    MaterialTheme.extendedColors.onSuccessContainer
                else MaterialTheme.extendedColors.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
