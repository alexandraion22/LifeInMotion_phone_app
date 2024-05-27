package com.example.healthapp.screens.home

import android.util.Log
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
import com.example.healthapp.BottomBarScreen
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
import com.example.healthapp.graphs.Graph
import com.example.healthapp.screens.crop

@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        topBar = { TopBar(navController = navController) },
        bottomBar = { BottomBar(navController = navController) }
    ) {
        HomeNavGraph(navController = navController)
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
            title = { Text("Health App") },
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
                        shape = MaterialTheme.shapes.medium // Apply rounded corners here
                    ) {
                        Column(
                            modifier = Modifier.padding(0.dp)
                        ) {
                            DropdownMenuItem(
                                onClick = { navController.navigate("SETTINGS") }
                            ) {
                                Text("Settings")
                            }
                            Divider()
                            DropdownMenuItem(
                                onClick = {
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
            modifier = Modifier.height(72.dp) // Set the desired height here
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
                        MaterialTheme.colors.secondaryVariant
                    }
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 8.dp)
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
