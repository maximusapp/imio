package com.globaldevmax.app.imio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.globaldevmax.app.imio.core.network.ConnectivityChecker
import com.globaldevmax.app.imio.ui.navigation.AppRoute
import com.globaldevmax.app.imio.ui.navigation.bottomNavDestinations
import com.globaldevmax.app.imio.ui.screen.favorite.FavoriteScreen
import com.globaldevmax.app.imio.ui.screen.home.HomeScreen
import com.globaldevmax.app.imio.ui.screen.profile.ProfileScreen
import com.globaldevmax.app.imio.ui.screen.splash.SplashScreen
import com.globaldevmax.app.imio.ui.screen.video.VideoScreen
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import org.koin.compose.koinInject

@Composable
fun ImioApp() {
    val navController = rememberNavController()
    val connectivityChecker = koinInject<ConnectivityChecker>()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val shouldShowBottomBar = currentDestination?.route in bottomNavDestinations.map { it.route.route }

    ImioGradientBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (shouldShowBottomBar) {
                    ImioBottomNavigationBar(currentDestination = currentDestination) { route ->
                        navController.navigate(route.route) {
                            popUpTo(AppRoute.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppRoute.Splash.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(AppRoute.Splash.route) {
                    SplashScreen(
                        connectivityChecker = connectivityChecker,
                        onInternetAvailable = {
                            navController.navigate(AppRoute.Home.route) {
                                popUpTo(AppRoute.Splash.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
                composable(AppRoute.Home.route) {
                    HomeScreen(
                        onVideoClick = { videoId ->
                            navController.navigate(AppRoute.Video.createRoute(videoId))
                        }
                    )
                }
                composable(AppRoute.Favorite.route) {
                    FavoriteScreen()
                }
                composable(AppRoute.Profile.route) {
                    ProfileScreen()
                }
                composable(
                    route = AppRoute.Video.route,
                    arguments = listOf(
                        navArgument(AppRoute.Video.ARG_VIDEO_ID) {
                            type = NavType.StringType
                        }
                    )
                ) { entry ->
                    VideoScreen(
                        videoId = entry.arguments?.getString(AppRoute.Video.ARG_VIDEO_ID).orEmpty()
                    )
                }
            }
        }
    }
}

@Composable
private fun ImioGradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ImioGradientTop, ImioGradientBottom)
                )
            )
    ) {
        content()
    }
}

@Composable
private fun ImioBottomNavigationBar(
    currentDestination: NavDestination?,
    onDestinationClick: (AppRoute) -> Unit
) {
    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = ImioGradientBottom.copy(alpha = 0.88f),
        tonalElevation = 0.dp
    ) {
        bottomNavDestinations.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == destination.route.route
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationClick(destination.route) },
                icon = {
                    Icon(
                        painter = painterResource(destination.iconResId),
                        contentDescription = stringResource(destination.labelResId)
                    )
                },
                label = { Text(text = stringResource(destination.labelResId)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = ImioGradientTop,
                    unselectedIconColor = Color.White.copy(alpha = 0.58f),
                    unselectedTextColor = Color.White.copy(alpha = 0.58f)
                )
            )
        }
    }
}
