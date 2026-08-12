package com.mahad.easyshopping.ui.home

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mahad.easyshopping.data.SessionManager
import com.mahad.easyshopping.data.model.Product
import com.mahad.easyshopping.ui.cart.CartViewModel
import com.mahad.easyshopping.ui.order.OrderHistoryContent
import com.mahad.easyshopping.ui.order.OrderViewModel
import java.util.Locale

sealed class HomeTab(val title: String, val icon: ImageVector) {
    object Home : HomeTab("Home", Icons.Filled.Home)
    object Categories : HomeTab("Categories", Icons.Filled.Category)
    object MyOrders : HomeTab("My Orders", Icons.Filled.ShoppingCart)
    object Help : HomeTab("Help", Icons.AutoMirrored.Filled.Help)
    object Account : HomeTab("Account", Icons.Filled.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onLoginClick: () -> Unit,
    onAddressClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onOrderDetailsClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf<HomeTab>(HomeTab.Home) }

    val tabs = listOf(
        HomeTab.Home,
        HomeTab.Categories,
        HomeTab.MyOrders,
        HomeTab.Help,
        HomeTab.Account
    )

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            text = "Easy Shopping",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { selectedTab = HomeTab.Account }) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (SessionManager.isLoggedIn) {
                                    onCartClick()
                                } else {
                                    onLoginClick()
                                }
                            }
                        ) {
                            BadgedBox(
                                badge = {
                                    if (cartUiState.itemCount > 0) {
                                        Badge {
                                            Text(cartUiState.itemCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
                HomeSearchBar()
            }
        },
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                HomeTab.Home -> {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.products) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) }
                                )
                            }
                        }
                    }
                }
                HomeTab.Account -> {
                    AccountScreen(
                        onLoginClick = onLoginClick,
                        onAddressClick = onAddressClick,
                        onOrdersClick = { selectedTab = HomeTab.MyOrders },
                        onLogoutSuccess = { selectedTab = HomeTab.Home },
                        homeViewModel = viewModel,
                        cartViewModel = cartViewModel
                    )
                }
                HomeTab.MyOrders -> {
                    if (SessionManager.isLoggedIn) {
                        OrderHistoryContent(
                            onOrderClick = onOrderDetailsClick,
                            viewModel = orderViewModel
                        )
                    } else {
                        LoginRequiredView(onLoginClick = onLoginClick)
                    }
                }
                else -> {
                    // Placeholder for other tabs
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = selectedTab.icon,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${selectedTab.title} Screen",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Coming Soon",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    MaterialTheme {
        AccountScreen(onLoginClick = {}, onAddressClick = {}, onOrdersClick = {}, onLogoutSuccess = {})
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onCartClick = {}, 
            onProductClick = {}, 
            onLoginClick = {}, 
            onAddressClick = {},
            onOrdersClick = {},
            onOrderDetailsClick = {}
        )
    }
}

@Composable
fun LoginRequiredView(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Please login to see this content", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLoginClick) {
            Text("Login")
        }
    }
}

@Composable
fun AccountScreen(
    onLoginClick: () -> Unit, 
    onAddressClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    homeViewModel: HomeViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        homeViewModel.logout(onSuccess = {
                            cartViewModel.clearCart()
                            onLogoutSuccess()
                        })
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (!SessionManager.isLoggedIn) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Please login to see your profile", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onLoginClick) {
                Text("Login")
            }
        }
        return
    }

    val menuItems = listOf(
        Pair("My Orders", Icons.AutoMirrored.Filled.ListAlt),
        Pair("Shipping Address", Icons.Default.LocationOn),
        Pair("Payment Methods", Icons.Default.Payment),
        Pair("My Wishlist", Icons.Default.Favorite),
        Pair("Settings", Icons.Default.Settings),
        Pair("Logout", Icons.AutoMirrored.Filled.ExitToApp)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            ProfileHeader(
                userName = SessionManager.userName ?: "User",
                email = SessionManager.userEmail ?: ""
            )
        }
        items(menuItems) { item ->
            val context = LocalContext.current
            ListItem(
                headlineContent = { Text(item.first) },
                leadingContent = { 
                    Icon(
                        imageVector = item.second, 
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    ) 
                },
                trailingContent = { 
                    Icon(
                        imageVector = Icons.Default.ChevronRight, 
                        contentDescription = null,
                        tint = Color.Gray
                    ) 
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        when (item.first) {
                            "My Orders" -> onOrdersClick()
                            "Shipping Address" -> onAddressClick()
                            "Logout" -> {
                                showLogoutDialog = true
                            }
                            else -> {
                                // Other items not implemented yet
                            }
                        }
                    }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun ProfileHeader(userName: String, email: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = androidx.compose.foundation.shape.CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun HomeSearchBar() {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp),
        placeholder = { Text("Search for products, brands and more") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        singleLine = true
    )
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
                if (product.isNew) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "NEW",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${String.format(Locale.US, "%,.2f", product.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Surface(
                        color = Color(0xFFFFF9C4),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFFFFA000)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = product.rating.rate.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
