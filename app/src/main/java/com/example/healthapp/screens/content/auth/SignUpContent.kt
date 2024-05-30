package com.example.healthapp.screens.content.auth
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.healthapp.R
import com.example.healthapp.graphs.Graph
import com.example.healthapp.ui.theme.CoolGray
import com.example.healthapp.ui.theme.customTextFieldColors
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpContent(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val auth = FirebaseAuth.getInstance()
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.Gray,
        backgroundColor = Color.LightGray.copy(alpha = 0.4f)
    )

    fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate(Graph.HOME) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                } else {
                    errorMessage = task.exception?.message
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(errorMessage ?: "Unknown error")
                    }
                }
            }
    }

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = { CustomSnackbarHost(it) }
        ) {
            SignUpForm(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                onSignUpClick = {
                    keyboardController?.hide()
                    if (email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()) {
                        if (password == confirmPassword) {
                            signUpUser(email, password)
                        } else {
                            errorMessage = "Passwords do not match."
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(errorMessage ?: "Unknown error")
                            }
                        }
                    } else {
                        errorMessage = "Please fill in all fields."
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(errorMessage ?: "Unknown error")
                        }
                    }
                },
                passwordFocusRequester = passwordFocusRequester,
                confirmPasswordFocusRequester = confirmPasswordFocusRequester,
                onDone = { keyboardController?.hide() },
                textFieldColors = customTextFieldColors()
            )
        }
    }
}

@Composable
fun SignUpForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    onSignUpClick: () -> Unit,
    passwordFocusRequester: FocusRequester,
    confirmPasswordFocusRequester: FocusRequester,
    onDone: () -> Unit,
    textFieldColors: TextFieldColors
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "E-mail",
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
            textFieldColors = textFieldColors
        )
        CustomPasswordField(
            value = password,
            onValueChange = onPasswordChange,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = onPasswordVisibilityChange,
            passwordFocusRequester = passwordFocusRequester,
            onDone = { confirmPasswordFocusRequester.requestFocus() },
            textFieldColors = textFieldColors
        )
        CustomPasswordField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = onPasswordVisibilityChange,
            passwordFocusRequester = confirmPasswordFocusRequester,
            onDone = { onDone() },
            textFieldColors = textFieldColors
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        ) {
            Text(
                "Sign Up",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}
