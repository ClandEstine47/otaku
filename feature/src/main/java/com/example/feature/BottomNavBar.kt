package com.example.feature

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource

@Composable
fun BottomNavBar(
    navBarItems: List<NavBarItem>,
    navigate: (String) -> Unit,
) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar {
        navBarItems.forEachIndexed { index, navBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navigate(navBarItem.title)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = if (selectedTabIndex == index) navBarItem.iconEnabled else navBarItem.iconDisabled),
                        contentDescription = "NavBar Icon",
                    )
                },
                label = { Text(navBarItem.title) },
            )
        }
    }
}

data class NavBarItem(
    val title: String,
    val iconEnabled: Int,
    val iconDisabled: Int,
)
