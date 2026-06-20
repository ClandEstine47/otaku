package com.example.core.navigation

import androidx.navigation.NavController

fun NavController.navigateAndReplaceStartRoute(newHomeRoute: OtakuScreen) {
    navigate(newHomeRoute) {
        popUpTo(graph.id) {
            inclusive = true
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
