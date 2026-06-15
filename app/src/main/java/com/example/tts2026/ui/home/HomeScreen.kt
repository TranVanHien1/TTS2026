package com.example.tts2026.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tts2026.R
import com.example.tts2026.data.auth.Entity.CarEntity
import com.example.tts2026.data.auth.Entity.UserEntity

private const val USERS_TAB = 0
private const val CARS_TAB = 1

@Composable
fun HomeRoute(
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val logoutCompleted by viewModel.logoutCompleted.collectAsStateWithLifecycle()

    LaunchedEffect(logoutCompleted) {
        if (logoutCompleted) {
            viewModel.consumeLogout()
            onLogout()
        }
    }

    HomeScreen(
        uiState = uiState,
        onTabSelected = viewModel::selectTab,
        onAddCar = viewModel::addCar,
        onUpdateCar = viewModel::updateCar,
        onDeleteCar = viewModel::deleteCar,
        onLogout = viewModel::logout
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onTabSelected: (Int) -> Unit,
    onAddCar: (String, String, Int, Double) -> Unit,
    onUpdateCar: (CarEntity) -> Unit,
    onDeleteCar: (CarEntity) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddCarDialog by rememberSaveable { mutableStateOf(false) }
    var carToEdit by remember { mutableStateOf<CarEntity?>(null) }
    var carToDelete by remember { mutableStateOf<CarEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = uiState.selectedTab == USERS_TAB,
                    onClick = { onTabSelected(USERS_TAB) },
                    icon = { Text("U") },
                    label = { Text(stringResource(R.string.users_tab)) }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == CARS_TAB,
                    onClick = { onTabSelected(CARS_TAB) },
                    icon = { Text("C") },
                    label = { Text(stringResource(R.string.cars_tab)) }
                )
            }
        },
        floatingActionButton = {
            if (uiState.selectedTab == CARS_TAB) {
                FloatingActionButton(onClick = { showAddCarDialog = true }) {
                    Text("+")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.home_greeting, uiState.email),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onLogout) {
                    Text(stringResource(R.string.logout))
                }
            }

            when (uiState.selectedTab) {
                USERS_TAB -> UserList(users = uiState.users)
                CARS_TAB -> CarList(
                    cars = uiState.cars,
                    onEditCar = { carToEdit = it },
                    onDeleteCar = { carToDelete = it }
                )
            }
        }
    }

    if (showAddCarDialog) {
        CarFormDialog(
            car = null,
            onDismiss = { showAddCarDialog = false },
            onConfirm = { name, model, year, price ->
                onAddCar(name, model, year, price)
                showAddCarDialog = false
            }
        )
    }

    carToEdit?.let { car ->
        CarFormDialog(
            car = car,
            onDismiss = { carToEdit = null },
            onConfirm = { name, model, year, price ->
                onUpdateCar(
                    car.copy(
                        name = name,
                        model = model,
                        year = year,
                        price = price
                    )
                )
                carToEdit = null
            }
        )
    }

    carToDelete?.let { car ->
        DeleteCarDialog(
            car = car,
            onDismiss = { carToDelete = null },
            onConfirm = {
                onDeleteCar(car)
                carToDelete = null
            }
        )
    }
}

@Composable
private fun UserList(users: List<UserEntity>) {
    Text(
        text = stringResource(R.string.registered_accounts),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    UserTableHeader()
    HorizontalDivider()

    if (users.isEmpty()) {
        Text(text = stringResource(R.string.no_registered_accounts))
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(
                items = users,
                key = { _, user -> user.email }
            ) { index, user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    TableCell(text = (index + 1).toString(), weight = 0.7f)
                    TableCell(text = user.email, weight = 2f)
                    TableCell(text = user.password, weight = 1.5f)
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun UserTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        TableCell(
            text = stringResource(R.string.account_index),
            weight = 0.7f,
            fontWeight = FontWeight.Bold
        )
        TableCell(
            text = stringResource(R.string.email),
            weight = 2f,
            fontWeight = FontWeight.Bold
        )
        TableCell(
            text = stringResource(R.string.password),
            weight = 1.5f,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CarList(
    cars: List<CarEntity>,
    onEditCar: (CarEntity) -> Unit,
    onDeleteCar: (CarEntity) -> Unit
) {
    Text(
        text = stringResource(R.string.car_list_title),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    if (cars.isEmpty()) {
        Text(text = stringResource(R.string.no_cars))
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = cars,
                key = { car -> car.id }
            ) { car ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = car.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(stringResource(R.string.car_model_value, car.model))
                        Text(stringResource(R.string.car_year_value, car.year))
                        Text(stringResource(R.string.car_price_value, car.price))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { onEditCar(car) }) {
                                Text(stringResource(R.string.edit))
                            }
                            TextButton(onClick = { onDeleteCar(car) }) {
                                Text(
                                    text = stringResource(R.string.delete),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarFormDialog(
    car: CarEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Double) -> Unit
) {
    var name by remember(car) { mutableStateOf(car?.name.orEmpty()) }
    var model by remember(car) { mutableStateOf(car?.model.orEmpty()) }
    var year by remember(car) { mutableStateOf(car?.year?.toString().orEmpty()) }
    var price by remember(car) { mutableStateOf(car?.price?.toString().orEmpty()) }
    var showError by remember(car) { mutableStateOf(false) }

    val parsedYear = year.toIntOrNull()
    val parsedPrice = price.toDoubleOrNull()
    val isValid = name.isNotBlank() &&
        model.isNotBlank() &&
        parsedYear != null &&
        parsedYear > 0 &&
        parsedPrice != null &&
        parsedPrice >= 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (car == null) R.string.add_car else R.string.edit_car
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.car_name)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text(stringResource(R.string.car_model)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text(stringResource(R.string.car_year)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(stringResource(R.string.car_price)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                if (showError && !isValid) {
                    Text(
                        text = stringResource(R.string.car_input_error),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onConfirm(
                            name.trim(),
                            model.trim(),
                            requireNotNull(parsedYear),
                            requireNotNull(parsedPrice)
                        )
                    } else {
                        showError = true
                    }
                }
            ) {
                Text(
                    stringResource(
                        if (car == null) R.string.add else R.string.save
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun DeleteCarDialog(
    car: CarEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_car)) },
        text = {
            Text(stringResource(R.string.delete_car_confirmation, car.name))
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun RowScope.TableCell(
    text: String,
    weight: Float,
    fontWeight: FontWeight? = null
) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .padding(horizontal = 4.dp),
        fontWeight = fontWeight
    )
}
