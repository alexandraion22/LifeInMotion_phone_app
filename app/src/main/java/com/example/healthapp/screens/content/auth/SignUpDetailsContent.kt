package com.example.healthapp.screens.content.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import com.example.healthapp.database.users.User
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.graphs.Graph
import com.example.healthapp.screens.content.home.profilePage.getValidationMessage
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.customTextFieldColors
import com.example.healthapp.utils.calculateBMI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpDetailsContent(navController: NavHostController, userViewModel: UserViewModel) {
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

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = { CustomSnackbarHost(it, Modifier.padding(bottom = 12.dp)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Personalise your experience",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Please complete the following fields in order to have a more personalised experience.",
                    fontSize = 15.sp,
                    color = colors.primaryVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CustomTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Full Name",
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(onDone = { ageFocusRequester.requestFocus() }),
                    textFieldColors = textFieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))
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

                Spacer(modifier = Modifier.height(8.dp))
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

                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField(
                    value = weightString,
                    onValueChange = { weightString = it},
                    label = "Weight (kg)",
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    keyboardType = KeyboardType.Number,
                    textFieldColors = textFieldColors,
                    focusRequester = weightFocusRequester
                )

                Spacer(modifier = Modifier.height(12.dp))
                DropdownList(
                    itemList = genders,
                    selectedIndex = selectedIndex,
                    onItemClick = { selectedIndex = it },
                    width = 0.915f
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "* If you do not know the exact values or do not want to disclose them, you can enter an approx. value.",
                    fontSize = 12.sp,
                    color = colors.primaryVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))
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
                            userViewModel.deleteAllUsers()
                            userViewModel.insert(userData)
                            navController.navigate(Graph.HOME) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        } else {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    validationMessage
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colors.secondary)
                ) {
                    Text(
                        "Continue",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownList(itemList: List<String>, selectedIndex: Int, onItemClick: (Int) -> Unit, width: Float) {
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    var handleDismissRequest by rememberSaveable { mutableStateOf(false) }

    val displayList = listOf(itemList[selectedIndex]) + itemList.filterIndexed { index, _ -> index != selectedIndex }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    if (showDropdown) RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    ) else RoundedCornerShape(16.dp)
                )
                .clickable {
                    handleDismissRequest = false
                    showDropdown = !showDropdown
                }
                .background(colors.primary)
                .border(
                    1.dp,
                    KindaLightGray,
                    if (showDropdown) RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    ) else RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = itemList[selectedIndex],
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            }
        }

        if (showDropdown) {
            Popup(
                alignment = Alignment.TopCenter,
                properties = PopupProperties(
                    excludeFromSystemGesture = true,
                ),
                onDismissRequest = {
                    if (!handleDismissRequest) {
                        showDropdown = false
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(width)
                        .clip(RoundedCornerShape(16.dp))
                        .heightIn(max = 180.dp)
                        .border(1.dp, KindaLightGray, RoundedCornerShape(16.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    displayList.onEachIndexed { index, item ->
                        if (index != 0) {
                            Divider(thickness = 1.dp, color = Color.LightGray)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.primary)
                                .clickable {
                                    onItemClick(itemList.indexOf(item))
                                    showDropdown = false
                                },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = item,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterStart)
                            )
                        }
                    }
                }
            }
        }
    }
}