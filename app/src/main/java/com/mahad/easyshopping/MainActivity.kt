package com.mahad.easyshopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mahad.easyshopping.data.SessionManager
import com.mahad.easyshopping.ui.cart.CartViewModel
import com.mahad.easyshopping.ui.create.CreateAccountScreen
import com.mahad.easyshopping.ui.forgotpassword.ForgotPasswordScreen
import com.mahad.easyshopping.ui.home.HomeScreen
import com.mahad.easyshopping.ui.login.LoginScreen
import com.mahad.easyshopping.ui.cart.CartScreen
import com.mahad.easyshopping.ui.details.ProductDetailsScreen
import com.mahad.easyshopping.ui.address.AddressListScreen
import com.mahad.easyshopping.ui.address.AddEditAddressScreen
import com.mahad.easyshopping.ui.checkout.CheckoutScreen
import com.mahad.easyshopping.ui.order.OrderSuccessScreen
import com.mahad.easyshopping.data.model.Address
import com.mahad.easyshopping.data.model.Order
import com.mahad.easyshopping.ui.order.OrderHistoryScreen
import com.mahad.easyshopping.ui.order.OrderDetailsScreen
import com.mahad.easyshopping.ui.order.ReviewScreen
import com.mahad.easyshopping.ui.address.AddressViewModel
import com.mahad.easyshopping.ui.theme.EasyShoppingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(this)
        enableEdgeToEdge()
        setContent {
            EasyShoppingTheme {
                val navController = rememberNavController()
                val cartViewModel: CartViewModel = viewModel()
                val addressViewModel: AddressViewModel = viewModel()
                
                NavHost(navController = navController, startDestination = "home") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                cartViewModel.fetchCart()
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            onCreateAccountClick = {
                                navController.navigate("create_account")
                            },
                            onForgotPasswordClick = {
                                navController.navigate("forgot_password")
                            },
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("create_account") {
                        CreateAccountScreen(onBackClick = {
                            navController.popBackStack()
                        })
                    }
                    composable("forgot_password") {
                        ForgotPasswordScreen(onBackClick = {
                            navController.popBackStack()
                        })
                    }
                    composable("home") {
                        HomeScreen(
                            onProductClick = { productId ->
                                navController.navigate("product_details/$productId")
                            },
                            onCartClick = {
                                navController.navigate("cart")
                            },
                            onLoginClick = {
                                navController.navigate("login")
                            },
                            onAddressClick = {
                                navController.navigate("address_list")
                            },
                            onOrdersClick = {
                                navController.navigate("order_history")
                            },
                            onOrderDetailsClick = { orderId ->
                                navController.navigate("order_details/$orderId")
                            },
                            cartViewModel = cartViewModel
                        )
                    }
                    composable("product_details/{productId}") { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull() ?: 0
                        ProductDetailsScreen(
                            productId = productId,
                            onBackClick = { navController.popBackStack() },
                            onCartClick = { navController.navigate("cart") },
                            onLoginRequired = {
                                navController.navigate("login")
                            },
                            cartViewModel = cartViewModel
                        )
                    }
                    composable("cart") {
                        CartScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onCheckoutClick = {
                                navController.navigate("checkout")
                            },
                            viewModel = cartViewModel
                        )
                    }
                    composable("address_list") {
                        AddressListScreen(
                            onBackClick = { navController.popBackStack() },
                            onAddAddressClick = { navController.navigate("add_edit_address") },
                            onEditAddressClick = { address ->
                                navController.currentBackStackEntry?.savedStateHandle?.set("address", address)
                                navController.navigate("add_edit_address")
                            },
                            viewModel = addressViewModel
                        )
                    }
                    composable("add_edit_address") {
                        val address = navController.previousBackStackEntry?.savedStateHandle?.get<Address>("address")
                        AddEditAddressScreen(
                            address = address,
                            onBackClick = { navController.popBackStack() },
                            onSuccess = { navController.popBackStack() },
                            viewModel = addressViewModel
                        )
                    }
                    composable("checkout") {
                        CheckoutScreen(
                            onBackClick = { navController.popBackStack() },
                            onSuccess = {
                                cartViewModel.clearCart()
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onAddAddressClick = {
                                navController.navigate("add_edit_address")
                            }
                        )
                    }
                    composable("order_success") {
                        val order = navController.previousBackStackEntry?.savedStateHandle?.get<Order>("order")
                        if (order != null) {
                            OrderSuccessScreen(
                                order = order,
                                onContinueShoppingClick = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onViewOrderDetailsClick = { orderId ->
                                    navController.navigate("order_details/$orderId")
                                }
                            )
                        }
                    }
                    composable("order_history") {
                        OrderHistoryScreen(
                            onBackClick = { navController.popBackStack() },
                            onOrderClick = { orderId ->
                                navController.navigate("order_details/$orderId")
                            }
                        )
                    }
                    composable("order_details/{orderId}") { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                        OrderDetailsScreen(
                            orderId = orderId,
                            onBackClick = { navController.popBackStack() },
                            onRateItemClick = { oId, itemId ->
                                navController.navigate("rate_item/$oId/$itemId")
                            },
                            onReorderSuccess = {
                                navController.navigate("cart")
                            },
                            onCancelSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("rate_item/{orderId}/{itemId}") { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                        val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                        ReviewScreen(
                            orderId = orderId,
                            itemId = itemId,
                            onBackClick = { navController.popBackStack() },
                            onSuccess = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
