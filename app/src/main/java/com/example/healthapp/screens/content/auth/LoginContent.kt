package com.example.healthapp.screens.content.auth

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.healthapp.R
import com.example.healthapp.graphs.Graph
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginContent(
    navController: NavHostController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val auth = FirebaseAuth.getInstance()

    val passwordFocusRequester = remember { FocusRequester() }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    navController.navigate(Graph.HOME) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                } else {
                    // Login failed
                    errorMessage = task.exception?.message
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(errorMessage ?: "Unknown error")
                    }
                }
            }
    }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.Gray,
        backgroundColor = Color.LightGray.copy(alpha = 0.4f)
    )
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(
                    hostState = it,
                    snackbar = { snackbarData ->
                        Snackbar(
                            snackbarData,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    },
                    modifier = Modifier.padding(bottom = 100.dp) // Adjust this value to show Snackbar higher up
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        textColor = Color(0xFF333333),
                        focusedLabelColor = Color(0xFF333333),
                        unfocusedLabelColor = Color(0xFF333333),
                        cursorColor = Color(0xFF333333)
                    )
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .focusRequester(passwordFocusRequester)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            R.drawable.ic_visibility_on
                        else
                            R.drawable.ic_visibility_off
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(id = image),
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        textColor = Color(0xFF333333),
                        focusedLabelColor = Color(0xFF333333),
                        unfocusedLabelColor = Color(0xFF333333),
                        cursorColor = Color(0xFF333333)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        keyboardController?.hide()
                        if (email.isNotBlank() && password.isNotBlank()) {
                            loginUser(email, password)
                        } else {
                            errorMessage = "Please enter both the e-mail and the password"
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(errorMessage ?: "Unknown error")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp), // Width takes 60% of the parent width
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary) // PurpleButton color
                ) {
                    Text(
                        "Login",
                        color = MaterialTheme.colors.primary,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Don’t have an account?",
                    style = MaterialTheme.typography.body1,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    modifier = Modifier.clickable { /* TODO */},
                    text = "Sign up",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
