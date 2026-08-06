package com.mahad.easyshopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mahad.easyshopping.ui.create.CreateAccountScreen
import com.mahad.easyshopping.ui.forgotpassword.ForgotPasswordScreen
import com.mahad.easyshopping.ui.home.HomeScreen
import com.mahad.easyshopping.ui.login.LoginScreen
import com.mahad.easyshopping.ui.theme.EasyShoppingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyShoppingTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onCreateAccountClick = {
                                navController.navigate("create_account")
                            },
                            onForgotPasswordClick = {
                                navController.navigate("forgot_password")
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
                        HomeScreen()
                    }
                }
            }
        }
    }
}
