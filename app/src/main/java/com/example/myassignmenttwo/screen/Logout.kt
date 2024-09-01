package com.example.myassignmenttwo.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.myassignmenttwo.navigation.Routes
import com.example.myassignmenttwo.navigation.SharedViewModel

@Composable
fun Logout(navController: NavController,viewModel: SharedViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.logoutUser() // Clear the user ID
        navController.navigate(Routes.login) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Logging out...")
    }
}