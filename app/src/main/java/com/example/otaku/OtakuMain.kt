package com.example.otaku

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.core.navigation.NavActionManager
import com.example.otaku.ui.theme.OtakuTheme

@Composable
fun OtakuMain() {
    OtakuTheme {
        val navController = rememberNavController()
        val navActionManager = NavActionManager.rememberNavActionManager(navController)

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            MainNavigation(
                navController = navController,
                navActionManager = navActionManager,
                deepLink = null,
            )
        }
    }
}
