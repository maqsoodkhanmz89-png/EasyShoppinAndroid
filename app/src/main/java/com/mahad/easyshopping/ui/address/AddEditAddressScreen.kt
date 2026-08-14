package com.mahad.easyshopping.ui.address

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mahad.easyshopping.data.model.Address
import com.mahad.easyshopping.data.model.AddressRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAddressScreen(
    address: Address? = null,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AddressViewModel = viewModel()
) {
    var type by remember { mutableStateOf(address?.type ?: "home") }
    var fullName by remember { mutableStateOf(address?.fullName ?: "") }
    var addressLine1 by remember { mutableStateOf(address?.addressLine1 ?: "") }
    var addressLine2 by remember { mutableStateOf(address?.addressLine2 ?: "") }
    var city by remember { mutableStateOf(address?.city ?: "") }
    var state by remember { mutableStateOf(address?.state ?: "") }
    var country by remember { mutableStateOf(address?.country ?: "") }
    var zipCode by remember { mutableStateOf(address?.zipCode ?: "") }
    var phoneNumber by remember { mutableStateOf(address?.phone ?: address?.phoneNumber ?: "") }
    var isDefault by remember { mutableStateOf(address?.isDefault ?: false) }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (address == null) "Add New Address" else "Edit Address") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Address Type", style = MaterialTheme.typography.titleMedium)
            AddressTypeSelector(selectedType = type, onTypeSelected = { type = it })

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = addressLine1,
                onValueChange = { addressLine1 = it },
                label = { Text("Address Line 1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = addressLine2,
                onValueChange = { addressLine2 = it },
                label = { Text("Address Line 2 (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = zipCode,
                    onValueChange = { zipCode = it },
                    label = { Text("Zip Code") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Mobile Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
                Text("Set as default address", modifier = Modifier.padding(start = 8.dp))
            }

            if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val request = AddressRequest(
                        fullName = fullName,
                        type = type,
                        addressLine1 = addressLine1,
                        addressLine2 = addressLine2,
                        street = addressLine1,
                        city = city,
                        state = state,
                        country = country,
                        zipCode = zipCode,
                        phone = phoneNumber,
                        phoneNumber = phoneNumber,
                        isDefault = isDefault
                    )
                    if (address == null) {
                        viewModel.addAddress(request, onSuccess)
                    } else {
                        viewModel.updateAddress(address.id, request, onSuccess)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !uiState.isLoading && fullName.isNotBlank() && addressLine1.isNotBlank() && city.isNotBlank() && state.isNotBlank() && country.isNotBlank() && zipCode.isNotBlank() && phoneNumber.isNotBlank()
            )
{
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Address")
                }
            }
        }
    }
}

@Composable
fun AddressTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val options = listOf("home", "office", "other")
    Row(Modifier.selectableGroup()) {
        options.forEach { text ->
            Row(
                Modifier
                    .weight(1f)
                    .height(48.dp)
                    .selectable(
                        selected = (text == selectedType),
                        onClick = { onTypeSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedType),
                    onClick = null
                )
                Text(
                    text = text.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
