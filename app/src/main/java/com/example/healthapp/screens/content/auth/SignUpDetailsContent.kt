package com.example.healthapp.screens.content.auth

import android.app.Application
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.healthapp.graphs.Graph
import com.example.healthapp.ui.theme.customTextFieldColors
import kotlinx.coroutines.launch

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val age: Int,
    val height: Int,
    val weight: Int,
    val gender: String
)

// DAO
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table ORDER BY id DESC LIMIT 1")
    suspend fun getUser(): User?
}

// Database
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: android.content.Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Repository
class UserRepository(private val userDao: UserDao) {
    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getUser(): User? {
        return userDao.getUser()
    }
}

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun insert(user: User) = viewModelScope.launch {
        repository.insert(user)
    }

    suspend fun getUser(): User? {
        return repository.getUser()
    }
}

class UserViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpDetailsContent(navController: NavHostController, userViewModel: UserViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var fullName by remember { mutableStateOf("Alexandra Ion") }
    var age by remember { mutableStateOf(17) }
    var height by remember { mutableStateOf(20) }
    var weight by remember { mutableStateOf(40) }
    val textFieldColors = customTextFieldColors()
    val genders = listOf("Female", "Male")
    var selectedIndex by rememberSaveable { mutableStateOf(0) }

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
            color = colors.primaryVariant,
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
            value = age.toString(),
            onValueChange = { age = it.toInt() },
            label = "Age",
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { /* Handle done action if needed */ }),
            keyboardType = KeyboardType.Number,
            textFieldColors = textFieldColors
        )

        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = height.toString(),
            onValueChange = { height = it.toInt() },
            label = "Height (cm)",
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { /* Handle done action if needed */ }),
            keyboardType = KeyboardType.Number,
            textFieldColors = textFieldColors
        )

        Spacer(modifier = Modifier.height(12.dp))
        CustomTextField(
            value = weight.toString(),
            onValueChange = { weight = it.toInt()},
            label = "Weight (kg)",
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { /* Handle done action if needed */ }),
            keyboardType = KeyboardType.Number,
            textFieldColors = textFieldColors
        )

        Spacer(modifier = Modifier.height(12.dp))
        DropdownList(itemList = genders, selectedIndex = selectedIndex, onItemClick = { selectedIndex = it })

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "* If you do not know the exact values or do not want to disclose them, you can enter an approx. value.",
            fontSize = 12.sp,
            color = MaterialTheme.colors.primaryVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = {
                keyboardController?.hide()
                val user = User(
                    fullName = fullName,
                    age = age,
                    height = height,
                    weight = weight,
                    gender = genders[selectedIndex]
                )
                userViewModel.insert(user)
                Log.d("SignUpDetailsContent", "User data saved: $user")
                navController.navigate(Graph.HOME) {
                    popUpTo(navController.graph.id) { inclusive = true }
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

@Composable
fun DropdownList(itemList: List<String>, selectedIndex: Int, onItemClick: (Int) -> Unit) {
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
                .clip(if (showDropdown) RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp) else RoundedCornerShape(16.dp))
                .clickable {
                    handleDismissRequest = false
                    showDropdown = !showDropdown
                }
                .background(colors.primary)
                .border(1.dp, Color(0xFFE2E8F0), if (showDropdown) RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp) else RoundedCornerShape(16.dp)),
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
                        .fillMaxWidth(0.915f)
                        .clip(RoundedCornerShape(16.dp))
                        .heightIn(max = 180.dp)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
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
