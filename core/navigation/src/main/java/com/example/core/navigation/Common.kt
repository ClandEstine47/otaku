package com.example.core.navigation

import androidx.navigation.NavController

fun NavController.navigateAndReplaceStartRoute(newHomeRoute: OtakuScreen) {
    navigate(newHomeRoute) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
