package com.example.playbox2.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.playbox2.presentation.offline.OfflineScreen
import com.example.playbox2.presentation.offline.OfflineViewModel
import com.example.playbox2.presentation.offline.OfflineViewModelFactory
import com.example.playbox2.presentation.splash.SplashScreen
import com.example.playbox2.presentation.videolist.VideoListScreen
import com.example.playbox2.presentation.videolist.VideoListViewModel
import com.example.playbox2.presentation.videoplayer.VideoPlayerScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    videoListViewModel: VideoListViewModel
) {
    // your app red color

    val systemUiController = rememberSystemUiController()
    val topBarColor = Color(0xFFF63B51)
    SideEffect {
        systemUiController.setStatusBarColor(
            color = topBarColor,
            darkIcons = false     // white icons → looks like your screenshot
        )

        systemUiController.setNavigationBarColor(
            color = Color.White,
            darkIcons = true
        )
    }
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // hide top and bottom on splash and video player
    val hideBars =
        currentRoute == "splash" ||
                currentRoute?.startsWith("videoplayer") == true

    Scaffold(
        topBar = {
            if (!hideBars) {
                FloatingTopBar(title = "PlayBox")
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
            startDestination = "splash",
            modifier = Modifier.padding(
                top = padding.calculateTopPadding() + 4.dp,
                bottom = padding.calculateBottomPadding() + 4.dp
            )
        ) {

            composable("splash") { SplashScreen(navController) }

            composable("videolist") {
                VideoListScreen(videoListViewModel, navController)
            }

            composable(
                route = "videoplayer/{videoUrl}",
                arguments = listOf(navArgument("videoUrl") { type = NavType.StringType })
            ) { entry ->
                val encodedUrl = entry.arguments?.getString("videoUrl") ?: ""
                val decodedUrl =
                    URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())

                VideoPlayerScreen(
                    videoUrl = decodedUrl,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("offline") {
                val offlineViewModel: OfflineViewModel = viewModel(
                    factory = OfflineViewModelFactory(videoListViewModel.repository)
                )
                OfflineScreen(viewModel = offlineViewModel)
            }
        }
    }
}

/* -------------------------- FLOATING TOP BAR -------------------------- */

@Composable
fun FloatingTopBar(
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF63B51))   // 🔴 SAME COLOR as top bar
            .statusBarsPadding()             // ✔ below battery/network safely
            .padding(
                top = 4.dp,
                bottom = 10.dp
            )
    ) {
        Surface(
            shape = RoundedCornerShape(2.dp),
            color = Color(0xFFF63B51),


        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    }


    /* -------------------------- FLOATING BOTTOM BAR -------------------------- */

}

@Composable
fun BottomNavBar(navController: NavController) {

    val currentRoute = navController.currentDestination?.route

    var selectedTab by remember {
        mutableStateOf(
            if (currentRoute == "offline") 1 else 0
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()     // prevents overlap with system buttons
            .padding(bottom = 4.dp)
    ) {

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            indicator = {
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(it[selectedTab]),
                    color = Color(0xFFF63B51),
                    height = 3.dp
                )
            },
            divider = {}
        ) {

            /* ------------------- HOME TAB ------------------- */

            Tab(
                selected = selectedTab == 0,
                onClick = {
                    selectedTab = 0
                    navController.navigate("videolist") { launchSingleTop = true }
                },
                selectedContentColor = Color(0xFFF63B51),
                unselectedContentColor = Color.Gray
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (selectedTab == 0) Color(0xFFF63B51) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Home",
                        color = if (selectedTab == 0) Color(0xFFF63B51) else Color.Gray
                    )
                }
            }

            /* ------------------- DOWNLOADS TAB ------------------- */

            Tab(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    navController.navigate("offline") { launchSingleTop = true }
                },
                selectedContentColor = Color(0xFFF63B51),
                unselectedContentColor = Color.Gray
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Downloads",
                        tint = if (selectedTab == 1) Color(0xFFF63B51) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Downloads",
                        color = if (selectedTab == 1) Color(0xFFF63B51) else Color.Gray
                    )
                }
            }
        }
    }
}
