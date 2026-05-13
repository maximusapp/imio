package com.globaldevmax.app.imio.ui.navigation

import androidx.annotation.StringRes
import androidx.annotation.DrawableRes
import com.globaldevmax.app.imio.R

sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Home : AppRoute("home")
    data object Favorite : AppRoute("favorite")
    data object Profile : AppRoute("profile")
    data object Premium : AppRoute("premium")
    data object PrivacyPolicy : AppRoute("privacy_policy")
    data object ParentMode : AppRoute("parent_mode")

    data object Video : AppRoute("video/{videoId}") {
        const val ARG_VIDEO_ID = "videoId"

        fun createRoute(videoId: String): String = "video/$videoId"
    }
}

data class BottomNavDestination(
    val route: AppRoute,
    @StringRes val labelResId: Int,
    @DrawableRes val iconResId: Int
)

val bottomNavDestinations = listOf(
    BottomNavDestination(
        route = AppRoute.Home,
        labelResId = R.string.bottom_nav_home,
        iconResId = R.drawable.ic_home
    ),
    BottomNavDestination(
        route = AppRoute.Favorite,
        labelResId = R.string.bottom_nav_favorite,
        iconResId = R.drawable.ic_favorite
    ),
    BottomNavDestination(
        route = AppRoute.Profile,
        labelResId = R.string.bottom_nav_profile,
        iconResId = R.drawable.ic_profile
    )
)
