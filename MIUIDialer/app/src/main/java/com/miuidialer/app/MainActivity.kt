package com.miuidialer.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.miuidialer.app.ui.screens.ContactsScreen
import com.miuidialer.app.ui.screens.DialerScreen
import com.miuidialer.app.ui.screens.REQUIRED_PERMISSIONS
import com.miuidialer.app.ui.screens.PermissionsScreen
import com.miuidialer.app.ui.theme.MIUIDialerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MIUIDialerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var hasPermissions by remember {
        mutableStateOf(false)
    }

    NavHost(navController = navController, startDestination = "permissions") {
        composable("permissions") {
            PermissionsScreen(
                onPermissionsGranted = {
                    navController.navigate("dialer") {
                        popUpTo("permissions") { inclusive = true }
                    }
                }
            )
        }
        composable("dialer") {
            DialerScreen(
                onNavigateToContacts = { navController.navigate("contacts") }
            )
        }
        composable("contacts") {
            ContactsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
