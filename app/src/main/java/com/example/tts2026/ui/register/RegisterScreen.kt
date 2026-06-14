package com.example.tts2026.ui.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tts2026.R

@Composable
fun RegisterRoute(
    onBackClick: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.register_success)
    val failureMessage = stringResource(R.string.register_failed)

    LaunchedEffect(uiState.registerSucceeded, uiState.registerFailed) {
        when {
            uiState.registerSucceeded -> {
                snackbarHostState.showSnackbar(successMessage)
                viewModel.consumeRegisterResult()
                onBackClick()
            }

            uiState.registerFailed -> {
                snackbarHostState.showSnackbar(failureMessage)
                viewModel.consumeRegisterResult()
            }
        }
    }

    RegisterScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onRegisterClick = viewModel::register,
        onBackClick = onBackClick
    )
}

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    snackbarHostState: SnackbarHostState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                label = { Text(stringResource(R.string.email)) },
                singleLine = true,
                isError = uiState.emailError,
                supportingText = {
                    if (uiState.emailError) {
                        Text(stringResource(R.string.email_required))
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                label = { Text(stringResource(R.string.password)) },
                singleLine = true,
                isError = uiState.passwordError,
                visualTransformation = PasswordVisualTransformation(),
                supportingText = {
                    if (uiState.passwordError) {
                        Text(stringResource(R.string.password_error))
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                label = { Text(stringResource(R.string.confirm_password)) },
                singleLine = true,
                isError = uiState.confirmPasswordError,
                visualTransformation = PasswordVisualTransformation(),
                supportingText = {
                    if (uiState.confirmPasswordError) {
                        Text(stringResource(R.string.confirm_password_error))
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onRegisterClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.register))
                }
            }

            TextButton(
                onClick = onBackClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back_to_login))
            }
        }
    }
}
