package com.globaldevmax.app.imio.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.core.network.ConnectivityChecker
import com.globaldevmax.app.imio.ui.navigation.AppRoute
import com.globaldevmax.app.imio.ui.navigation.bottomNavDestinations
import com.globaldevmax.app.imio.ui.screen.favorite.FavoriteScreen
import com.globaldevmax.app.imio.ui.screen.home.HomeScreen
import com.globaldevmax.app.imio.ui.screen.parentmode.ParentModeScreen
import com.globaldevmax.app.imio.ui.screen.premium.PremiumScreen
import com.globaldevmax.app.imio.ui.screen.privacy.PrivacyPolicyScreen
import com.globaldevmax.app.imio.ui.screen.profile.ProfileScreen
import com.globaldevmax.app.imio.ui.screen.splash.SplashScreen
import com.globaldevmax.app.imio.ui.screen.video.VideoScreen
import com.globaldevmax.app.imio.ui.theme.FredokaFontFamily
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import org.koin.compose.koinInject
import kotlinx.coroutines.delay

@Composable
fun ImioApp() {
    val navController = rememberNavController()
    val connectivityChecker = koinInject<ConnectivityChecker>()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val shouldShowBottomBar = currentDestination?.route in bottomNavDestinations.map { it.route.route }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var isParentModeActive by remember { mutableStateOf(false) }
    var allowedMinutes by remember { mutableStateOf("") }
    var showSleepDialog by remember { mutableStateOf(false) }
    var showParentChallenge by remember { mutableStateOf(false) }
    var challengeAnswer by remember { mutableStateOf("") }
    var showWrongAnswer by remember { mutableStateOf(false) }
    val challengeLeft by remember { mutableIntStateOf(6) }
    val challengeRight by remember { mutableIntStateOf(7) }
    val navigateToBottomDestination: (AppRoute) -> Unit = { route ->
        navController.navigate(route.route) {
            popUpTo(AppRoute.Home.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    LaunchedEffect(isParentModeActive, allowedMinutes) {
        val minutes = allowedMinutes.toLongOrNull()
        if (isParentModeActive && minutes != null && minutes > 0) {
            delay(minutes * MILLIS_IN_MINUTE)
            if (isParentModeActive) {
                showSleepDialog = true
            }
        }
    }

    ImioGradientBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (shouldShowBottomBar && !isLandscape) {
                    ImioBottomNavigationBar(
                        currentDestination = currentDestination,
                        onDestinationClick = navigateToBottomDestination
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ImioNavHost(
                    navController = navController,
                    connectivityChecker = connectivityChecker,
                    isParentModeActive = isParentModeActive,
                    allowedMinutes = allowedMinutes,
                    onParentModeActiveChange = { active ->
                        isParentModeActive = active
                        if (!active) {
                            showSleepDialog = false
                            showParentChallenge = false
                        }
                    },
                    onAllowedMinutesChange = { allowedMinutes = it },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                )

                if (shouldShowBottomBar && isLandscape) {
                    ImioNavigationRail(
                        currentDestination = currentDestination,
                        onDestinationClick = navigateToBottomDestination
                    )
                }
            }
        }
        if (showSleepDialog) {
            SleepDialog(
                onCloseClick = {
                    challengeAnswer = ""
                    showWrongAnswer = false
                    showSleepDialog = false
                    showParentChallenge = true
                }
            )
        }
        if (showParentChallenge) {
            ParentChallengeDialog(
                left = challengeLeft,
                right = challengeRight,
                answer = challengeAnswer,
                showWrongAnswer = showWrongAnswer,
                onAnswerChange = { challengeAnswer = it.filter(Char::isDigit) },
                onSubmit = {
                    if (challengeAnswer.toIntOrNull() == challengeLeft * challengeRight) {
                        isParentModeActive = false
                        showSleepDialog = false
                        showParentChallenge = false
                        challengeAnswer = ""
                        showWrongAnswer = false
                    } else {
                        showWrongAnswer = true
                    }
                }
            )
        }
    }
}

@Composable
private fun ImioNavHost(
    navController: NavHostController,
    connectivityChecker: ConnectivityChecker,
    isParentModeActive: Boolean,
    allowedMinutes: String,
    onParentModeActiveChange: (Boolean) -> Unit,
    onAllowedMinutesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route,
        modifier = modifier
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
                onRetryClick = {
                    // TODO: Recall Home API when the data layer is implemented.
                }
            )
        }
        composable(AppRoute.Favorite.route) {
            FavoriteScreen()
        }
        composable(AppRoute.Profile.route) {
            ProfileScreen(
                onPremiumClick = { navController.navigate(AppRoute.Premium.route) },
                onPrivacyPolicyClick = { navController.navigate(AppRoute.PrivacyPolicy.route) },
                onParentModeClick = { navController.navigate(AppRoute.ParentMode.route) }
            )
        }
        composable(AppRoute.Premium.route) {
            PremiumScreen(
                onSubscribeClick = {
                    // TODO: Connect billing flow for the Premium subscription.
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(AppRoute.PrivacyPolicy.route) {
            PrivacyPolicyScreen(url = stringResource(R.string.privacy_policy_url))
        }
        composable(AppRoute.ParentMode.route) {
            ParentModeScreen(
                isParentModeActive = isParentModeActive,
                allowedMinutes = allowedMinutes,
                onParentModeActiveChange = onParentModeActiveChange,
                onAllowedMinutesChange = onAllowedMinutesChange,
                onBackClick = { navController.popBackStack() }
            )
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
                videoId = entry.arguments?.getString(AppRoute.Video.ARG_VIDEO_ID).orEmpty(),
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun SleepDialog(
    onCloseClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        title = {
            Row {
                Text(
                    text = stringResource(R.string.sleep_dialog_title),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onCloseClick) {
                    Text(text = stringResource(R.string.action_close_icon))
                }
            }
        },
        text = {
            Text(text = stringResource(R.string.sleep_dialog_message))
        }
    )
}

@Composable
private fun ParentChallengeDialog(
    left: Int,
    right: Int,
    answer: String,
    showWrongAnswer: Boolean,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.parent_challenge_title)) },
        text = {
            Column {
                Text(text = stringResource(R.string.parent_challenge_question, left, right))
                OutlinedTextField(
                    value = answer,
                    onValueChange = onAnswerChange,
                    label = { Text(text = stringResource(R.string.parent_challenge_answer_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (showWrongAnswer) {
                    Text(
                        text = stringResource(R.string.parent_challenge_wrong_answer),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSubmit) {
                Text(text = stringResource(R.string.action_submit))
            }
        }
    )
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

private const val MILLIS_IN_MINUTE = 60_000L

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
                label = {
                    Text(
                        text = stringResource(destination.labelResId),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FredokaFontFamily
                        )
                    )
                },
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

@Composable
private fun ImioNavigationRail(
    currentDestination: NavDestination?,
    onDestinationClick: (AppRoute) -> Unit
) {
    NavigationRail(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = ImioGradientBottom.copy(alpha = 0.88f)
    ) {
        bottomNavDestinations.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == destination.route.route
            } == true

            NavigationRailItem(
                selected = selected,
                onClick = { onDestinationClick(destination.route) },
                icon = {
                    Icon(
                        painter = painterResource(destination.iconResId),
                        contentDescription = stringResource(destination.labelResId)
                    )
                },
                label = {
                    Text(
                        text = stringResource(destination.labelResId),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FredokaFontFamily
                        )
                    )
                },
                colors = NavigationRailItemDefaults.colors(
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
