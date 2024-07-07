package com.example.koseemani

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.koseemani.navigation.Contacts
import com.example.koseemani.navigation.History
import com.example.koseemani.navigation.Home
import com.example.koseemani.navigation.Settings
import com.example.koseemani.ui.home.HomeScreen
import com.example.koseemani.ui.theme.KoseemaniTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            )
        )
        super.onCreate(savedInstanceState)
        setContent {
            KoseemaniApp()

        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KoseemaniApp() {
    KoseemaniTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize()
                .safeDrawingPadding()
                .statusBarsPadding()
            ,
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            Scaffold(bottomBar = { BottomNavigationBar(navController = navController) }) {

                NavHost(navController = navController, startDestination = Home) {
                    composable<Home> {
                        HomeScreen(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        )
                    }
                    composable<History> {
                        Box(contentAlignment = Alignment.Center) {
                            Text("History")
                        }
                    }
                    composable<Contacts> {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Contacts")
                        }
                    }
                    composable<Settings> {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Settings")
                        }
                    }

                }

            }
        }
    }
}


@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var selectedIndex by rememberSaveable {
        mutableStateOf(0)
    }
    val bottomNavItems = listOf<BottomNavItem>(
        BottomNavItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home,
            route = Home
        ),
        BottomNavItem(
            title = "Contacts",
            selectedIcon = Icons.Filled.AccountCircle,
            unSelectedIcon = Icons.Outlined.AccountCircle,
            route = Contacts
        ),
        BottomNavItem(
            title = "History",
            selectedIcon = Icons.Filled.Refresh,
            unSelectedIcon = Icons.Outlined.Refresh,
            route = History
        ),
        BottomNavItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unSelectedIcon = Icons.Outlined.Settings,
            route = Settings
        ),
    )
    NavigationBar(containerColor = Color.Transparent) {
        bottomNavItems.forEachIndexed { index, bottomNavItem ->

            NavigationBarItem(
                label = { Text(text = bottomNavItem.title) },
                selected = index == selectedIndex,
                onClick = {
                    navController.navigate(bottomNavItem.route)

                    selectedIndex = index
                },
                icon = {
                    Icon(
                        imageVector = if (index == selectedIndex) {
                            bottomNavItem.selectedIcon
                        } else {
                            bottomNavItem.unSelectedIcon
                        },

                        contentDescription = null,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.surface,
                    unselectedIconColor = MaterialTheme.colorScheme.outline,
                    unselectedTextColor = MaterialTheme.colorScheme.outline


                )
            )
        }

    }
}


data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val route: Any
)

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    KoseemaniTheme {
//        Greeting("Android")
//    }
//}