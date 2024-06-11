package com.example.healthapp.screens.content.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.database.users.User
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
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
                Text(text = "Go to Bpm screen")
            }
        }
    }
}
