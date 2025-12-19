package com.example.playbox2.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.playbox2.presentation.splash.SplashScreen
import com.example.playbox2.presentation.videolist.VideoListScreen
import com.example.playbox2.presentation.videoplayer.VideoPlayerScreen
import com.example.playbox2.presentation.videolist.VideoListViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(viewModel: VideoListViewModel) {

    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val hideBars =
       currentRoute?.startsWith("videoplayer") == true ||
                currentRoute == "splash"


    Scaffold(

        topBar = {
            if (!hideBars) {
                TopAppBar(
                    title = {
                        Text(
                            text = "PlayBox",
                            color = Color.White
                        )
                    }
                    ,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFF63B51)
                    )
                )
            }
        },

        bottomBar = {
            if (!hideBars) {
                BottomNavBar(navController)
            }
        }

    ) { padding ->

        NavHost(
            navController = navController,
            modifier = Modifier.padding(padding),
            startDestination = "splash"
        ) {

            composable("splash") {
                SplashScreen(navController)
            }

            composable("videolist") {
                VideoListScreen(viewModel, navController)
            }

            composable(
                "videoplayer/{videoUrl}",
                arguments = listOf(navArgument("videoUrl") { type = NavType.StringType })
            ) { backStackEntry ->

                val encoded = backStackEntry.arguments?.getString("videoUrl") ?: ""
                val decodedUrl = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())

                VideoPlayerScreen(
                    videoUrl = decodedUrl,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}


@Composable
fun BottomNavBar(navController: NavController) {

    BottomAppBar(
        containerColor = Color(0xFFF63B51)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            BottomBarIcon(
                icon = Icons.Default.Home,
                label = "Home",
                route = "videolist",
                navController = navController
            )

//            BottomBarIcon(
//                icon = Icons.Default.PlayArrow,
//                label = "Player",
//                route = "videolist",
//                navController = navController
//            )

            BottomBarIcon(
                icon = Icons.Default.Settings,
                label = "Settings",
                route = "settings",
                navController = navController
            )
        }
    }
}

@Composable
fun BottomBarIcon(
    icon: ImageVector,
    label: String,
    route: String,
    navController: NavController
) {
    IconButton(
        onClick = {
            navController.navigate(route) {
                popUpTo("videolist") { inclusive = false }
                launchSingleTop = true
            }
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White
        )
    }
}


