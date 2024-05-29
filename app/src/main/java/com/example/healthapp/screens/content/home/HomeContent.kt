package com.example.healthapp.screens.content.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.healthapp.BottomBarScreen
import com.example.healthapp.graphs.Graph

@Composable
fun HomeContent(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
                navController.navigate("HOME/STEPS")
            }) {
            Text(text = "Go to Steps screen")
        }
    }
}
