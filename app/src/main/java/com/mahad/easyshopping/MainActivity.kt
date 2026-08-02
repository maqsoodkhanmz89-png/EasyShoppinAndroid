package com.mahad.easyshopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mahad.easyshopping.ui.create.CreateAccountScreen
import com.mahad.easyshopping.ui.forgotpassword.ForgotPasswordScreen
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

@Composable
fun HomeScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Handle create action */ },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Create") },
                text = { Text("Create") }
            )
        }
    ) { innerPadding ->
        Greeting(
            name = "to Easy Shopping!",
            modifier = Modifier.padding(innerPadding)
        )
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    EasyShoppingTheme {
        HomeScreen()
    }
}
