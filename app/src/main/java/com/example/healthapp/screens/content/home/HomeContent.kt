package com.example.healthapp.screens.content.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.healthapp.database.users.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeContent(navController: NavHostController, userViewModel: UserViewModel) {
    val scope = rememberCoroutineScope()
    var fullName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            val user = userViewModel.getUser()
            fullName = user?.fullName ?: "User"
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Hello, $fullName")
            Button(onClick = {
                navController.navigate("HOME/STEPS")
            }) {
                Text(text = "Go to Steps screen")
            }
        }
    }
}