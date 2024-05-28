package com.example.healthapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.example.healthapp.graphs.RootNavigationGraph
import com.example.healthapp.ui.theme.HealthAppTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthAppTheme {
                val isUserAuthenticated = remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()
                Log.d("TAG","1")
                LaunchedEffect(Unit) {
                    scope.launch(Dispatchers.IO) {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        isUserAuthenticated.value = currentUser != null
                    }
                }

                RootNavigationGraph(navController = rememberNavController(), isUserAuthenticated = isUserAuthenticated.value)
            }
        }
    }
}
