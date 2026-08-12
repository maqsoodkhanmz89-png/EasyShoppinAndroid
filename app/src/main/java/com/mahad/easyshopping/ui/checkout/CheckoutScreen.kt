package com.mahad.easyshopping.ui.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mahad.easyshopping.data.model.Address
import com.mahad.easyshopping.data.model.CardDetails
import com.mahad.easyshopping.data.model.Order
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onSuccess: (Order) -> Unit,
    onAddAddressClick: () -> Unit,
    viewModel: CheckoutViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var cardNumber by remember { mutableStateOf("") }
    var cardType by remember { mutableStateOf("Visa") }
    var holderName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
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
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Shipping Address Section
            SectionHeader(title = "Shipping Address", action = "Add New", onActionClick = onAddAddressClick)
            if (uiState.addresses.isEmpty()) {
                Text("No addresses found. Please add one.", color = Color.Gray)
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.addresses) { address ->
                        AddressCard(
                            address = address,
                            isSelected = uiState.selectedAddress?.id == address.id,
                            onClick = { viewModel.selectAddress(address) }
                        )
                    }
                }
            }

            // Payment Method Section
            SectionHeader(title = "Payment Method")
            PaymentMethodSelector(
                selectedMethod = uiState.paymentMethod,
                onMethodSelected = { viewModel.selectPaymentMethod(it) }
            )

            // Card Details (if applicable)
            if (uiState.paymentMethod == "credit_card" || uiState.paymentMethod == "debit_card") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Card Details", style = MaterialTheme.typography.titleMedium)
                    
                    Row(Modifier.selectableGroup()) {
                        listOf("Visa", "Mastercard", "AmEx").forEach { type ->
                            Row(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .selectable(
                                        selected = (cardType == type),
                                        onClick = { 
                                            cardType = type
                                            viewModel.updateCardDetails(CardDetails(cardNumber, cardType, holderName))
                                        },
                                        role = Role.RadioButton
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = (cardType == type), onClick = null)
                                Text(type, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { 
                            if (it.length <= 16) {
                                cardNumber = it
                                viewModel.updateCardDetails(CardDetails(cardNumber, cardType, holderName))
                            }
                        },
                        label = { Text("Card Number (16 digits)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = holderName,
                        onValueChange = { 
                            holderName = it
                            viewModel.updateCardDetails(CardDetails(cardNumber, cardType, holderName))
                        },
                        label = { Text("Card Holder Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Coupon Code
            OutlinedTextField(
                value = uiState.couponCode,
                onValueChange = { viewModel.updateCouponCode(it) },
                label = { Text("Coupon Code (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Notes
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.updateNotes(it) },
                label = { Text("Delivery Notes (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Order Summary
            HorizontalDivider()
            OrderSummary(subtotal = uiState.subtotal)

            if (uiState.errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = { viewModel.placeOrder(onSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Place Order")
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onActionClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (action != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(action)
            }
        }
    }
}

@Composable
fun AddressCard(address: Address, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick() },
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(text = address.type.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text(text = address.addressLine1, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
                Text(text = "${address.city}, ${address.state}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(text = address.phoneNumber, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
fun PaymentMethodSelector(selectedMethod: String, onMethodSelected: (String) -> Unit) {
    val methods = listOf(
        "credit_card" to "Credit Card",
        "debit_card" to "Debit Card",
        "paypal" to "PayPal",
        "google_pay" to "Google Pay",
        "cash_on_delivery" to "Cash on Delivery"
    )

    Column(Modifier.selectableGroup()) {
        methods.forEach { (id, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .selectable(
                        selected = (selectedMethod == id),
                        onClick = { onMethodSelected(id) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (selectedMethod == id), onClick = null)
                Text(text = label, modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}

@Composable
fun OrderSummary(subtotal: Double) {
    val tax = subtotal * 0.1 // 10% tax for example
    val shipping = if (subtotal > 500) 0.0 else 50.0
    val total = subtotal + tax + shipping

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Order Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        SummaryRow("Items Subtotal", subtotal)
        SummaryRow("Tax (10%)", tax)
        SummaryRow("Shipping Charges", shipping)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total Amount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                formatCurrency(total),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(formatCurrency(amount))
    }
}

fun formatCurrency(amount: Double): String {
    return "₹${String.format(Locale.US, "%,.2f", amount)}"
}
