package com.example.healthapp.screens.content.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.healthapp.R
import com.example.healthapp.graphs.Graph
import com.google.firebase.auth.FirebaseAuth

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

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = auth.currentUser
                    navController.navigate(Graph.HOME) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                } else {
                    // Login failed
                    errorMessage = task.exception?.message
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                }
            }
    }

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
            shape = RoundedCornerShape(16.dp), // Rounded corners here
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                textColor = Color(0xFF333333),
                focusedLabelColor = Color(0xFF333333),
                unfocusedLabelColor = Color(0xFF333333)
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
                    if (email.isNotBlank() && password.isNotBlank()) {
                        loginUser(email, password)
                    } else {
                        errorMessage = "Please enter both email and password."
                    }
                }
            ),
            shape = RoundedCornerShape(16.dp), // Rounded corners here
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                textColor = Color(0xFF333333),
                focusedLabelColor = Color(0xFF333333),
                unfocusedLabelColor = Color(0xFF333333)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                keyboardController?.hide()
                if (email.isNotBlank() && password.isNotBlank()) {
                    loginUser(email, password)
                } else {
                    errorMessage = "Please enter both e-mail and password."
                }
            },
            modifier = Modifier.fillMaxWidth(0.6f)
                .height(48.dp),// Width takes 60% of the parent width
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary) // PurpleButton color
        ) {
            Text(
                "Login",
                color = MaterialTheme.colors.primary,
                fontSize = 18.sp
            )
        }
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colors.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}