package com.example.healthapp.screens.mainscreens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.healthapp.graphs.HomeNavGraph
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.example.healthapp.R
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.bpm.last.BpmRepository
import com.example.healthapp.database.steps.daily.StepsDailyRepository
import com.example.healthapp.database.steps.hourly.StepsHourlyRepository
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.graphs.Graph
import com.example.healthapp.screens.crop
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel,
    bpmDailyRepository: BpmDailyRepository,
    bpmHourlyRepository: BpmHourlyRepository,
    stepsDailyRepository: StepsDailyRepository,
    stepsHourlyRepository: StepsHourlyRepository
) {
    Scaffold(
        topBar = { TopBar(navController = navController) },
        bottomBar = { BottomBar(navController = navController) }
    ) {
        HomeNavGraph(navController = navController, userViewModel = userViewModel, bpmDailyRepository = bpmDailyRepository, bpmHourlyRepository = bpmHourlyRepository, stepsDailyRepository = stepsDailyRepository, stepsHourlyRepository = stepsHourlyRepository)
    }
}

@Composable
fun TopBar(navController: NavHostController = rememberNavController()) {
    var expanded by remember { mutableStateOf(false) }
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Sleep,
        BottomBarScreen.Workout,
        BottomBarScreen.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { screen ->
        currentDestination?.route?.startsWith(screen.route) == true
    }
    if (bottomBarDestination) {
        TopAppBar(
            title = { Text("Life in Motion") },
            actions = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_menu), // Replace with your menu icon resource
                        contentDescription = "Menu Icon"
                    )
                }
                DropdownMenu(
                    modifier = Modifier
                        .crop(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.width(140.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(0.dp)
                        ) {
                            DropdownMenuItem(
                                onClick = { expanded = false
                                    navController.navigate("SETTINGS") }
                            ) {
                                Text("Settings")
                            }
                            Divider()
                            DropdownMenuItem(
                                onClick = {
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate(Graph.AUTHENTICATION) {
                                        popUpTo(navController.graph.id){
                                            inclusive = true
                                        }
                                    }
                                }
                            ) {
                                Text("Logout")
                            }
                        }
                    }
                }
            },
            elevation = 0.dp // Remove the elevation to make the line invisible
        )
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Sleep,
        BottomBarScreen.Workout,
        BottomBarScreen.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { screen ->
        currentDestination?.route?.startsWith(screen.route) == true
    }
    if (bottomBarDestination) {
        BottomNavigation(
            modifier = Modifier.height(60.dp) // Set the desired height here
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val isSelected = currentDestination?.hierarchy?.any { it.route?.startsWith(screen.route) == true } == true
    BottomNavigationItem(
        icon = {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = screen.icon),
                    contentDescription = "Navigation Icon",
                    tint = if (isSelected) {
                        MaterialTheme.colors.primaryVariant
                    } else {
                        MaterialTheme.colors.onPrimary
                    },
                    modifier = Modifier.size(36.dp)
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 4.dp)
                            .background(
                                color = MaterialTheme.colors.primaryVariant,
                                shape = CircleShape
                            )
                    )
                }
            }
        },
        selected = isSelected,
        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}
