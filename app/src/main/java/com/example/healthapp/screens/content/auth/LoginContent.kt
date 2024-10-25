package com.example.healthapp.screens.content.auth

import android.annotation.SuppressLint
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.healthapp.R
import com.example.healthapp.database.users.User
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.graphs.Graph
import com.example.healthapp.ui.theme.CoolGray
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.customTextFieldColors
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginContent(navController: NavHostController, userViewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val auth = FirebaseAuth.getInstance()
    val passwordFocusRequester = remember { FocusRequester() }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            user = userViewModel.getUser()
        }
    }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.Gray,
        backgroundColor = Color.LightGray.copy(alpha = 0.4f)
    )

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if(currentUser?.uid == (user?.uid ?: "")) {
                        navController.navigate(Graph.HOME) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                    else
                        navController.navigate("SIGNUP/DETAILS")
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
            snackbarHost = { CustomSnackbarHost(it, Modifier.padding(bottom = 140.dp)) }
        ) {
            LoginForm(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                onLoginClick = {
                    keyboardController?.hide()
                    if (email.isNotBlank() && password.isNotBlank()) {
                        loginUser(email, password)
                    } else {
                        errorMessage = "Please enter both the e-mail and the password."
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(errorMessage ?: "Unknown error")
                        }
                    }
                },
                passwordFocusRequester = passwordFocusRequester,
                onDone = { keyboardController?.hide() },
                textFieldColors = customTextFieldColors(),
                navController = navController
            )
        }
    }
}

@Composable
fun CustomSnackbarHost(snackbarHostState: SnackbarHostState, modifier: Modifier) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(
                modifier = Modifier.padding(horizontal = 16.dp),
                content = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = snackbarData.message,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        },
        modifier = modifier
    )
}

@Composable
fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    onLoginClick: () -> Unit,
    passwordFocusRequester: FocusRequester,
    onDone: () -> Unit,
    textFieldColors: TextFieldColors,
    navController: NavHostController
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
        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "E-mail",
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
            textFieldColors = textFieldColors,
        )
        CustomPasswordField(
            value = password,
            onValueChange = onPasswordChange,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = onPasswordVisibilityChange,
            passwordFocusRequester = passwordFocusRequester,
            onDone = onDone,
            textFieldColors = textFieldColors,
            label = "Password"
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        ) {
            Text(
                "Login",
                color = Color.White,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Don’t have an account?",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            modifier = Modifier.clickable { navController.navigate("SIGNUP")},
            text = "Sign up",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.primaryVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    keyboardType: KeyboardType = KeyboardType.Text,
    textFieldColors: TextFieldColors = TextFieldDefaults.textFieldColors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    ),
    focusRequester: FocusRequester = FocusRequester(),
    enabled: Boolean =  true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .focusRequester(focusRequester)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, KindaLightGray, RoundedCornerShape(16.dp)),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = keyboardType
        ),
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(16.dp),
        colors = textFieldColors,
        enabled = enabled
    )
}


@Composable
fun CustomPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    passwordFocusRequester: FocusRequester,
    onDone: () -> Unit,
    textFieldColors: TextFieldColors,
    label: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .focusRequester(passwordFocusRequester)
            .border(1.dp, KindaLightGray, RoundedCornerShape(16.dp)),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) R.drawable.ic_visibility_on else R.drawable.ic_visibility_off
            IconButton(onClick = onPasswordVisibilityChange) {
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = stringResource(R.string.toggle_password_visibility),
                    tint = CoolGray
                )
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        shape = RoundedCornerShape(16.dp),
        colors = textFieldColors
    )
}