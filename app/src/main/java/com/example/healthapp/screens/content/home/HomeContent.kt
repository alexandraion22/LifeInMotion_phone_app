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
    val users by userViewModel.users.collectAsState()

    LaunchedEffect(Unit) {
        scope.launch {
            val user = userViewModel.getUser()
            fullName = user?.fullName ?: "User"
            userViewModel.loadAllUsers()  // Load all users when the HomeContent is first displayed
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
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                scope.launch {
                    userViewModel.deleteAllUsers()
                }
            }) {
                Text(text = "Delete All Users")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "User Table", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))
            if (users.isNotEmpty()) {
                UserTable(users = users)
            } else {
                Text("No users found.")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun UserTable(users: List<User>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .background(Color.Gray)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            TableCell(text = "Full Name")
            TableCell(text = "Age")
            TableCell(text = "Height (cm)")
            TableCell(text = "Weight (kg)")
            TableCell(text = "Gender")
            TableCell(text = "Uid")
            TableCell(text = "Activity Level")
            TableCell(text = "BMI")
        }
        users.forEach { user ->
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                TableCell(text = user.fullName)
                TableCell(text = user.age.toString())
                TableCell(text = user.height.toString())
                TableCell(text = user.weight.toString())
                TableCell(text = user.gender)
                TableCell(text = user.uid)
                TableCell(text = user.activityLevel.toString())
                TableCell(text = user.bmi.toString())
            }
        }
    }
}

@Composable
fun TableCell(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(4.dp)
    )
}
