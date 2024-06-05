package com.example.healthapp.screens.content.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.healthapp.database.users.User
import com.example.healthapp.database.users.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileContent (navController: NavHostController, userViewModel: UserViewModel) {
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            user = userViewModel.getUser()
        }
    }

    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                Modifier
                    .fillMaxWidth(0.9f)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .padding(12.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()){
                    user?.let { Text(text = it.fullName, modifier = Modifier.align(Alignment.CenterHorizontally)) }
                    Column(horizontalAlignment = Alignment.Start) {
                        Row {
                            Text(text = "Age: ")
                            Text(text = user?.age.toString())
                        }
                        Row {
                            Text(text = "Height: ")
                            Text(text = user?.height.toString())
                        }
                        Row {
                            Text(text = "Weight: ")
                            Text(text = user?.weight.toString())
                        }
                        Row {
                            Text(text = "Gender: ")
                            Text(text = user?.gender ?: "Not specified")
                        }
                        Row {
                            Text(text = "BMI: ")
                            Text(text = user?.bmi.toString())
                        }
                        Row {
                            Text(text = "Activity Level: ")
                            Text(text = user?.activityLevel.toString())
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                Modifier
                    .fillMaxWidth(0.9f)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text="Weekly Summary")
                }
            }
        }
    }
}
