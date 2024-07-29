package com.example.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Immutable
class NavActionManager(
    private val navController: NavHostController,
) {
    companion object {
        @Composable
        fun rememberNavActionManager(navController: NavHostController = rememberNavController()) =
            remember {
                NavActionManager(navController)
            }
    }
}
