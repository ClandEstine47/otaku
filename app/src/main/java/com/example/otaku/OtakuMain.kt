package com.example.otaku

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.core.navigation.MainNavigation
import com.example.core.navigation.NavActionManager
import com.example.otaku.ui.theme.OtakuTheme

@Composable
fun OtakuMain() {
    OtakuTheme {
        val navController = rememberNavController()
        val navActionManager = NavActionManager.rememberNavActionManager(navController)

        MainNavigation(
            navController = navController,
            navActionManager = navActionManager,
            deepLink = null,
        )
    }
}