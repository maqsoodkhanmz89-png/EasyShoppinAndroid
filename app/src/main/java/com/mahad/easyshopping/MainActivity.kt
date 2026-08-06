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
                
                NavHost(navController = navController, startDestination = "home") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
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
                            viewModel = cartViewModel
                        )
                    }
                }
            }
        }
    }
}
