package com.example.healthapp.screens.content.home

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import com.example.healthapp.database.users.User
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.screens.content.auth.CustomSnackbarHost
import com.example.healthapp.screens.content.auth.CustomTextField
import com.example.healthapp.screens.content.auth.DropdownList
import com.example.healthapp.ui.theme.customTextFieldColors
import com.example.healthapp.utils.calculateBMI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileSettingsContent(navController: NavHostController, userViewModel: UserViewModel) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    var fullName by remember { mutableStateOf("") }
    var ageString  by remember { mutableStateOf("") }
    var weightString  by remember { mutableStateOf("") }
    var heightString by remember { mutableStateOf("") }
    val textFieldColors = customTextFieldColors()
    val genders = listOf("Female", "Male")
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val ageFocusRequester = remember { FocusRequester() }
    val heightFocusRequester = remember { FocusRequester() }
    val weightFocusRequester = remember { FocusRequester() }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val firebaseAuth = FirebaseAuth.getInstance()
    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.Gray,
        backgroundColor = Color.LightGray.copy(alpha = 0.4f)
    )

    var user by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(Unit) {
        scope.launch {
            user = userViewModel.getUser()
            fullName = user?.fullName ?: "User"
            ageString = user?.age.toString()
            weightString = user?.weight.toString()
            heightString = user?.height.toString()
            selectedIndex = if(user?.gender == "Female") 0 else 1
        }
    }

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = { CustomSnackbarHost(it, Modifier.padding(bottom = 68.dp)) }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 22.dp, top = 32.dp)
                ) {
                    Text(
                        text = "Profile settings",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 12.dp)
                    )

                    Text(
                        text = "Data visbile only to you",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 16.dp, start = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
                            .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CustomTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = "Full Name",
                                imeAction = ImeAction.Done,
                                keyboardActions = KeyboardActions(onDone = { ageFocusRequester.requestFocus() }),
                                textFieldColors = textFieldColors
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            CustomTextField(
                                value = ageString,
                                onValueChange = { ageString = it },
                                label = "Age",
                                imeAction = ImeAction.Done,
                                keyboardActions = KeyboardActions(onDone = { heightFocusRequester.requestFocus() }),
                                keyboardType = KeyboardType.Number,
                                textFieldColors = textFieldColors,
                                focusRequester = ageFocusRequester
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            CustomTextField(
                                value = heightString,
                                onValueChange = { heightString = it },
                                label = "Height (cm)",
                                imeAction = ImeAction.Done,
                                keyboardActions = KeyboardActions(onDone = { weightFocusRequester.requestFocus() }),
                                keyboardType = KeyboardType.Number,
                                textFieldColors = textFieldColors,
                                focusRequester = heightFocusRequester
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            CustomTextField(
                                value = weightString,
                                onValueChange = { weightString = it },
                                label = "Weight (kg)",
                                imeAction = ImeAction.Done,
                                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                                keyboardType = KeyboardType.Number,
                                textFieldColors = textFieldColors,
                                focusRequester = weightFocusRequester
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            DropdownList(
                                itemList = genders,
                                selectedIndex = selectedIndex,
                                onItemClick = { selectedIndex = it },
                                width = 0.832f
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            Row( Modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp)) {
                                Button(
                                    onClick = {
                                        keyboardController?.hide()
                                        val validationMessage =
                                            getValidationMessage(fullName, ageString, heightString, weightString)
                                        val uid = firebaseAuth.currentUser?.uid
                                        if (validationMessage == null) {
                                            val userData = User(
                                                uid = uid!!,
                                                fullName = fullName,
                                                age = ageString.toInt(),
                                                height = heightString.toDouble(),
                                                weight = weightString.toDouble(),
                                                gender = genders[selectedIndex],
                                                bmi = calculateBMI(weight = weightString.toDouble(), height = heightString.toDouble()),
                                                activityLevel = 0
                                            )
                                            userViewModel.insert(userData)
                                            navController.navigate("PROFILE")
                                        } else {
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    validationMessage
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .width(132.dp)
                                        .height(42.dp),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = colors.secondary)
                                ) {
                                    Text(
                                        "Save",
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.weight(0.2f))
                                Button(
                                    onClick = {
                                        navController.navigate("PROFILE")
                                    },
                                    modifier = Modifier
                                        .width(132.dp)
                                        .height(42.dp),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = colors.primaryVariant)
                                ) {
                                    Text(
                                        "Cancel",
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getValidationMessage(fullName: String, ageString: String, heightString: String, weightString: String): String? {
    val fullNamePattern = Regex("^[A-Z][a-z]*(\\s[A-Z][a-z]*)+$")

    if (fullName.isBlank() || !fullName.matches(fullNamePattern)) {
        return "The name must start with capital letters and contain the first and last names ."
    }

    try {
        val age = ageString.toInt()
        if (age !in 1..100) {
            return "The age must have a valid value."
        }
    } catch (e: NumberFormatException)  {
        return "The age must have a valid value."
    }
    try {
        val height = heightString.toDouble()
        if (height <= 0) {
            return "The height must have a valid value."
        }
    } catch (e: NumberFormatException)  {
        return "The height must have a valid value."
    }
    try {
        val weight = weightString.toDouble()
        if (weight <= 0) {
            return "The weight must have a valid value."
        }
    } catch (e: NumberFormatException)  {
        return "The weight must have a valid value."
    }

    return null
}