package com.example.feature.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.chrisbanes.haze.HazeState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomNavBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenBottomNavBarLoads_showsAllTabs() {
        // When
        composeTestRule.setContent {
            val hazeState = remember { HazeState() }

            MaterialTheme {
                BottomNavBar(
                    hazeState = hazeState,
                    tabIndex = 0,
                    navigate = {},
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("ANIME").assertExists()
        composeTestRule.onNodeWithText("MANGA").assertExists()
    }
}
