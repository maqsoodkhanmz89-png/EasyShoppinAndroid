package com.mahad.easyshopping.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mahad.easyshopping.data.model.Address
import com.mahad.easyshopping.data.model.Order
import com.mahad.easyshopping.data.model.OrderItem
import com.mahad.easyshopping.data.model.TrackingStage
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: String,
    onBackClick: () -> Unit,
    onRateItemClick: (String, String) -> Unit,
    onReorderSuccess: () -> Unit,
    onCancelSuccess: () -> Unit,
    viewModel: OrderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetails(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoadingDetails) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null && !uiState.isLoadingAction) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                uiState.selectedOrder?.let { order ->
                    OrderDetailsContent(
                        order = order,
                        trackingHistory = uiState.trackingHistory,
                        onCancelClick = { viewModel.cancelOrder(order.orderId, "User request", onCancelSuccess) },
                        onReorderClick = { viewModel.reorder(order.orderId, onReorderSuccess) },
                        onRateItemClick = { itemId -> onRateItemClick(order.orderId, itemId) },
                        isLoadingAction = uiState.isLoadingAction,
                        errorMessage = uiState.errorMessage
                    )
                }
            }
        }
    }
}

@Composable
fun OrderDetailsContent(
    order: Order,
    trackingHistory: List<TrackingStage>?,
    onCancelClick: () -> Unit,
    onReorderClick: () -> Unit,
    onRateItemClick: (String) -> Unit,
    isLoadingAction: Boolean,
    errorMessage: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (errorMessage != null) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage, 
                    color = MaterialTheme.colorScheme.onErrorContainer, 
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        // Order ID and Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Order #${order.orderId.takeLast(8).uppercase()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Placed on ${order.createdAt.take(10)}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            OrderStatusBadge(status = order.status)
        }

        // Items
        OrderSection(title = "Items", icon = Icons.Default.Info) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                order.items.forEach { item ->
                    OrderItemRow(item = item, showRateButton = order.status.lowercase() == "delivered", onRateClick = { onRateItemClick(item.productId) })
                }
            }
        }

        // Tracking
        if (!trackingHistory.isNullOrEmpty()) {
            OrderSection(title = "Tracking History", icon = Icons.Default.LocalShipping) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    trackingHistory.forEach { stage ->
                        TrackingStageRow(stage)
                    }
                }
            }
        }

        // Shipping Address
        OrderSection(title = "Shipping Address", icon = Icons.Default.LocationOn) {
            AddressInfo(address = order.shippingAddress)
        }

        // Payment Info
        OrderSection(title = "Payment", icon = Icons.Default.Payment) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = order.paymentMethod?.replace("_", " ")?.uppercase() ?: "N/A", style = MaterialTheme.typography.bodyLarge)
                Text(text = order.paymentStatus?.uppercase() ?: "PENDING", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }

        // Pricing Summary
        OrderSection(title = "Order Summary", icon = null) {
            PricingSummary(order = order)
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (order.status.lowercase() == "confirmed" || order.status.lowercase() == "processing") {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    enabled = !isLoadingAction
                ) {
                    Text("Cancel Order")
                }
            }
            Button(
                onClick = onReorderClick,
                modifier = Modifier.weight(1f),
                enabled = !isLoadingAction
            ) {
                if (isLoadingAction) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Reorder")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun OrderSection(title: String, icon: ImageVector?, content: @Composable () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.medium
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItem, showRateButton: Boolean, onRateClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = item.image,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.productTitle ?: item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(text = "Qty: ${item.quantity} • $${String.format(Locale.getDefault(), "%.2f", item.price)}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        if (showRateButton) {
            TextButton(onClick = onRateClick) {
                Text("Rate")
            }
        }
    }
}

@Composable
fun TrackingStageRow(stage: TrackingStage) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(text = stage.stage, style = MaterialTheme.typography.bodyLarge, fontWeight = if (stage.status == "completed") FontWeight.Bold else FontWeight.Normal)
            Text(text = stage.timestamp.take(16).replace("T", " "), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        if (stage.status == "completed") {
            Icon(Icons.Default.Info, contentDescription = "Completed", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun AddressInfo(address: Address) {
    Column {
        Text(text = address.fullName ?: "", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Text(text = address.street ?: address.addressLine1, style = MaterialTheme.typography.bodyMedium)
        Text(text = "${address.city}, ${address.state} ${address.zipCode}", style = MaterialTheme.typography.bodyMedium)
        Text(text = address.country, style = MaterialTheme.typography.bodyMedium)
        Text(text = "Phone: ${address.phone ?: address.phoneNumber}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PricingSummary(order: Order) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PricingRow("Subtotal", order.subtotal)
        if (order.discount > 0) PricingRow("Discount", -order.discount, color = Color(0xFF4CAF50))
        PricingRow("Tax", order.tax)
        PricingRow("Shipping", order.shippingCharges)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("$${String.format(Locale.getDefault(), "%.2f", order.total)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun PricingRow(label: String, amount: Double, color: Color = Color.Unspecified) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text("${if (amount < 0) "-" else ""}$${String.format(Locale.getDefault(), "%.2f", kotlin.math.abs(amount))}", style = MaterialTheme.typography.bodyMedium, color = color)
    }
}
