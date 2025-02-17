package com.wizeline.panamexicans.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wizeline.panamexicans.navigation.MainNavHost

@Composable
fun MainRoot(navController: NavController, viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val bottomBarNavController = rememberNavController()
    MainScreen(
        bottomBarNavController = bottomBarNavController,
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                else -> Unit
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onEvent: (MainUiEvents) -> Unit,
    uiState: MainUiState,
    bottomBarNavController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onEvent(MainUiEvents.OnProfileClicked) }) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                    }
                }, title = {
                    Text(text = "Welcome ${uiState.firstName} ${uiState.lastName}")
                })
        },
        bottomBar = {
            NavigationBar(
                contentColor = Color.White,
                containerColor = Color.Black
            ) {
                uiState.bottomNavScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(id = screen.second),
                                contentDescription = null
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xffff6600),
                            selectedTextColor = Color(0xffff6600),
                            unselectedIconColor = Color.White,
                            unselectedTextColor = Color.White,
                            indicatorColor = Color.Transparent
                        ),
                        label = { Text(text = screen.first) },
                        selected = screen.first == uiState.selectedTab,
                        onClick = {
                            onItemClicked(bottomBarNavController, screen.first)
                            onEvent(MainUiEvents.OnTabClicked(screen.first))
                        }
                    )
                }
            }
        }
    ) {
        MainNavHost(
            modifier = Modifier.padding(it),
            navController = bottomBarNavController
        )
    }
}

private fun onItemClicked(
    bottomBarNavController: NavHostController,
    screen: String,
) {
    bottomBarNavController.navigate(screen) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(bottomBarNavController.graph.findStartDestination().id) {
            inclusive = true
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreen(
        onEvent = {},
        uiState = MainUiState(),
        bottomBarNavController = rememberNavController()
    )
}
