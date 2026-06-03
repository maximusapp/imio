package com.globaldevmax.app.imio.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.globaldevmax.app.imio.network.connectivity.ConnectivityChecker
import com.globaldevmax.app.imio.core.evening.EveningModeStore
import com.globaldevmax.app.imio.core.locale.VideoLocaleStore
import com.globaldevmax.app.imio.core.parent.ParentModeStore
import com.globaldevmax.app.imio.core.parent.ParentModeState
import com.globaldevmax.app.imio.domain.repository.PremiumRepository
import com.globaldevmax.app.imio.ui.components.ParentVerificationDialog
import com.globaldevmax.app.imio.ui.navigation.AppRoute
import com.globaldevmax.app.imio.ui.navigation.bottomNavDestinations
import com.globaldevmax.app.imio.ui.screen.favorite.FavoriteScreen
import com.globaldevmax.app.imio.ui.screen.home.HomeScreen
import com.globaldevmax.app.imio.ui.screen.search.SearchScreen
import com.globaldevmax.app.imio.ui.screen.eveningmode.EveningModeScreen
import com.globaldevmax.app.imio.ui.screen.parentmode.ParentModeScreen
import com.globaldevmax.app.imio.ui.screen.premium.PremiumScreen
import com.globaldevmax.app.imio.ui.screen.privacy.PrivacyPolicyScreen
import com.globaldevmax.app.imio.ui.screen.profile.ProfileScreen
import com.globaldevmax.app.imio.ui.screen.splash.SplashScreen
import com.globaldevmax.app.imio.ui.screen.video.VideoScreen
import com.globaldevmax.app.imio.ui.screen.videolocale.VideoLocaleSetupScreen
import com.globaldevmax.app.imio.ui.theme.FredokaFontFamily
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun ImioApp() {
    val navController = rememberNavController()
    val connectivityChecker = koinInject<ConnectivityChecker>()
    val parentModeStore = koinInject<ParentModeStore>()
    val eveningModeStore = koinInject<EveningModeStore>()
    val videoLocaleStore = koinInject<VideoLocaleStore>()
    val premiumRepository = koinInject<PremiumRepository>()
    val scope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val shouldShowBottomBar = currentDestination?.route in bottomNavDestinations.map { it.route.route }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val parentModeState by parentModeStore.state.collectAsStateWithLifecycle(
        initialValue = ParentModeState()
    )
    val isParentModeActive = parentModeState.isActive
    val allowedMinutes = parentModeState.allowedMinutes
    val recentMinutes = parentModeState.recentMinutes
    val parentModeEndsAtMillis = parentModeState.endsAtMillis
    var showSleepDialog by remember(parentModeState) {
        mutableStateOf(
            parentModeState.sleepDialogVisible ||
                (parentModeState.isActive &&
                    parentModeState.endsAtMillis > 0L &&
                    parentModeState.endsAtMillis <= System.currentTimeMillis())
        )
    }
    var showParentChallenge by remember { mutableStateOf(false) }
    val isPremiumSubscriptionActive by premiumRepository.isPremiumActive.collectAsStateWithLifecycle()
    val isEveningModeActive by eveningModeStore.isActive.collectAsStateWithLifecycle(initialValue = false)
    val selectedVideoLocale by videoLocaleStore.preferredVideoLocale.collectAsStateWithLifecycle(initialValue = null)
    var hasActiveNotification by remember { mutableStateOf(false) }
    var splashLocaleGateReady by remember { mutableStateOf(false) }
    var splashHasVideoLocale by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        splashHasVideoLocale = videoLocaleStore.hasSelectedVideoLocale.first()
        splashLocaleGateReady = true
    }

    LaunchedEffect(Unit) {
        premiumRepository.start()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                premiumRepository.refreshPurchases()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val navigateToBottomDestination: (AppRoute) -> Unit = { route ->
        navController.navigate(route.route) {
            popUpTo(AppRoute.Home.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    LaunchedEffect(isParentModeActive, parentModeEndsAtMillis, showSleepDialog) {
        if (isParentModeActive && !showSleepDialog && parentModeEndsAtMillis > 0L) {
            val remainingMillis = parentModeEndsAtMillis - System.currentTimeMillis()
            if (remainingMillis > 0L) {
                delay(remainingMillis)
            }

            if (isParentModeActive) {
                showSleepDialog = true
                scope.launch { parentModeStore.showSleepDialog() }
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
                        if (active) {
                            val endsAtMillis = calculateParentModeEndsAtMillis(allowedMinutes)
                            showSleepDialog = false
                            scope.launch {
                                parentModeStore.activate(allowedMinutes, endsAtMillis)
                                parentModeStore.saveRecentMinute(allowedMinutes)
                            }
                        } else {
                            showParentChallenge = true
                        }
                    },
                    onAllowedMinutesChange = { minutes ->
                        scope.launch {
                            parentModeStore.saveAllowedMinutes(minutes)
                            if (isParentModeActive) {
                                val endsAtMillis = calculateParentModeEndsAtMillis(minutes)
                                showSleepDialog = false
                                parentModeStore.updateEndsAtMillis(endsAtMillis)
                            }
                        }
                    },
                    isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                    isEveningModeActive = isEveningModeActive,
                    hasActiveNotification = hasActiveNotification,
                    onEveningModeActiveChange = { active ->
                        scope.launch {
                            if (active) {
                                eveningModeStore.activate()
                            } else {
                                eveningModeStore.deactivate()
                            }
                        }
                    },
                    splashLocaleGateReady = splashLocaleGateReady,
                    splashHasVideoLocale = splashHasVideoLocale,
                    selectedVideoLocale = selectedVideoLocale,
                    recentMinutes = recentMinutes,
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
            ParentVerificationDialog(
                title = stringResource(R.string.sleep_dialog_message),
                actionText = stringResource(R.string.action_close),
                animationResId = remember {
                    listOf(
                        R.raw.ic_yay_jump,
                        R.raw.ic_crab_walk,
                        R.raw.ic_rabbit_with_balloon,
                        R.raw.ic_panda_sleeping,
                        R.raw.ic_lazy_doge_sleeping
                    ).random()
                },
                onConfirmed = {
                    showSleepDialog = false
                    showParentChallenge = false
                    scope.launch { parentModeStore.deactivate() }
                },
                onDismiss = { showSleepDialog = false }
            )
        }

        if (showParentChallenge) {
            ParentVerificationDialog(
                onConfirmed = {
                    showSleepDialog = false
                    showParentChallenge = false
                    scope.launch { parentModeStore.deactivate() }
                },
                onDismiss = { showParentChallenge = false }
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
    isPremiumSubscriptionActive: Boolean,
    isEveningModeActive: Boolean,
    hasActiveNotification: Boolean,
    onEveningModeActiveChange: (Boolean) -> Unit,
    splashLocaleGateReady: Boolean,
    splashHasVideoLocale: Boolean,
    selectedVideoLocale: String?,
    recentMinutes: List<String>,
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
                canProceed = splashLocaleGateReady,
                onInternetAvailable = {
                    val destination = if (splashHasVideoLocale) {
                        AppRoute.Home.route
                    } else {
                        AppRoute.VideoLocaleSetup.createRoute(fromProfile = false)
                    }
                    navController.navigate(destination) {
                        popUpTo(AppRoute.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = AppRoute.VideoLocaleSetup.route,
            arguments = listOf(
                navArgument(AppRoute.VideoLocaleSetup.ARG_FROM_PROFILE) {
                    type = NavType.BoolType
                }
            )
        ) { entry ->
            val fromProfile = entry.arguments?.getBoolean(AppRoute.VideoLocaleSetup.ARG_FROM_PROFILE) == true
            VideoLocaleSetupScreen(
                fromProfile = fromProfile,
                onContinue = {
                    if (fromProfile) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(AppRoute.Home.route) {
                            popUpTo(AppRoute.VideoLocaleSetup.createRoute(fromProfile = false)) {
                                inclusive = true
                            }
                        }
                    }
                },
                onBackClick = if (fromProfile) {
                    { navController.popBackStack() }
                } else {
                    null
                }
            )
        }
        composable(AppRoute.Home.route) {
            HomeScreen(
                isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                onVideoClick = { video ->
                    navController.navigate(AppRoute.Video.createRoute(video.id))
                }
            )
        }
        composable(AppRoute.Search.route) {
            SearchScreen(
                isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                onVideoClick = { video ->
                    navController.navigate(AppRoute.Video.createRoute(video.id))
                }
            )
        }
        composable(AppRoute.Favorite.route) {
            FavoriteScreen(
                isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                onVideoClick = { video ->
                    navController.navigate(AppRoute.Video.createRoute(video.id))
                }
            )
        }
        composable(AppRoute.Profile.route) {
            ProfileScreen(
                onPremiumClick = { navController.navigate(AppRoute.Premium.route) },
                onPrivacyPolicyClick = { navController.navigate(AppRoute.PrivacyPolicy.route) },
                onParentModeClick = { navController.navigate(AppRoute.ParentMode.route) },
                onEveningModeClick = { navController.navigate(AppRoute.EveningMode.route) },
                onVideoLanguageClick = {
                    navController.navigate(AppRoute.VideoLocaleSetup.createRoute(fromProfile = true))
                },
                selectedVideoLocale = selectedVideoLocale,
                isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                isParentModeActive = isParentModeActive,
                isEveningModeActive = isEveningModeActive,
                hasActiveNotification = hasActiveNotification
            )
        }
        composable(AppRoute.Premium.route) {
            PremiumScreen(
                onBackClick = { navController.popBackStack() },
                onSubscriptionActivated = { navController.popBackStack() }
            )
        }
        composable(AppRoute.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(AppRoute.ParentMode.route) {
            ParentModeScreen(
                isParentModeActive = isParentModeActive,
                allowedMinutes = allowedMinutes,
                recentMinutes = recentMinutes,
                onParentModeActiveChange = onParentModeActiveChange,
                onAllowedMinutesChange = onAllowedMinutesChange,
                showAds = !isPremiumSubscriptionActive,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(AppRoute.EveningMode.route) {
            EveningModeScreen(
                isEveningModeActive = isEveningModeActive,
                onEveningModeActiveChange = onEveningModeActiveChange,
                showAds = !isPremiumSubscriptionActive,
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
                isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                onBackClick = { navController.popBackStack() }
            )
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

private const val MILLIS_IN_MINUTE = 60_000L

private fun calculateParentModeEndsAtMillis(allowedMinutes: String): Long {
    val minutes = allowedMinutes.toLongOrNull() ?: return 0L
    return if (minutes > 0) {
        System.currentTimeMillis() + minutes * MILLIS_IN_MINUTE
    } else {
        0L
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
