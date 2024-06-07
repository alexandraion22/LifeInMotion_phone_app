package com.example.healthapp

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.database.users.UserViewModelFactory
import com.example.healthapp.graphs.Graph
import com.example.healthapp.graphs.RootNavigationGraph
import com.example.healthapp.ui.theme.HealthAppTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthAppTheme {
                val startRoute = remember { mutableStateOf(Graph.WELCOME) }
                val startDestination = remember { mutableStateOf("LOGIN") }
                val scope = rememberCoroutineScope()
                val userViewModel: UserViewModel = viewModel(
                    factory = UserViewModelFactory(application)
                )
                LaunchedEffect(Unit) {
                    scope.launch(Dispatchers.IO) {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val user = userViewModel.getUser()
                        val uid = user?.uid
                        if (uid != null) {
                            Log.d(TAG,uid)
                        }
                        if (user != null) {
                            Log.d(TAG,user.uid)
                        }
                        if(currentUser==null)
                            startRoute.value = Graph.AUTHENTICATION
                        else
                        {
                            if(currentUser.uid == uid)
                                startRoute.value = Graph.HOME
                            else
                            {
                                startRoute.value = Graph.AUTHENTICATION
                                startDestination.value = "SIGNUP/DETAILS"
                            }
                        }
                    }
                }
                RootNavigationGraph(navController = rememberNavController(), startRoute = startRoute.value, userViewModel = userViewModel, startDestinationPage = startDestination.value)
            }
        }
    }
}
