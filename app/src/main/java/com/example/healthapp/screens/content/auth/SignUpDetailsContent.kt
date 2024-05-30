package com.example.healthapp.screens.content.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.healthapp.R
import com.example.healthapp.graphs.Graph
import com.example.healthapp.ui.theme.customTextFieldColors

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpDetailsContent(navController: NavHostController) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    val textFieldColors = customTextFieldColors()

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
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "Please complete the following fields in order to have a more personalised experience.",
            fontSize = 15.sp,
            color = MaterialTheme.colors.primaryVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { /* Handle done action if needed */ }),
            textFieldColors = textFieldColors
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = age,
            onValueChange = { age = it },
            label = "Age",
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { /* Handle done action if needed */ }),
            keyboardType = KeyboardType.Number,
            textFieldColors = textFieldColors
        )

        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = height,
            onValueChange = { height = it },
            label = "Height (cm)",
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { /* Handle done action if needed */ }),
            keyboardType = KeyboardType.Number,
            textFieldColors = textFieldColors
        )

        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = weight,
            onValueChange = { weight = it },
            label = "Weight (kg)",
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { /* Handle done action if needed */ }),
            keyboardType = KeyboardType.Number,
            textFieldColors = textFieldColors
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "* If you do not  know the exact values or do not want to disclose them , you can enter an approx. value.",
            fontSize = 12.sp,
            color = MaterialTheme.colors.primaryVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = {
                keyboardController?.hide()
                Log.d("TAG",weight)
                navController.navigate(Graph.HOME) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        ) {
            Text(
                "Continue",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

enum class Gender(val stringResId: Int) {
    MALE(R.string.gender_male),
    FEMALE(R.string.gender_female)
}
