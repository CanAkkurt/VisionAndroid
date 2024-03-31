package de.yanneckreiss.cameraxtutorial.ui.bottomnav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController


@Composable
fun BottomNavigationT(
    navController: NavHostController,
    isBottomNavExpanded: Boolean,
    onToggleClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.BottomStart
    ) {
        AnimatedVisibility(
            visible = isBottomNavExpanded,
            enter = fadeIn() ,
            exit = fadeOut()
        ) {
            NavigationBar {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Building,
                    BottomNavItem.Analytics
                    // BottomNavItem.Profile is assumed to be the toggle, add if not
                )
                // Toggle button to collapse the bottom nav
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MoreVert, contentDescription = "Collapse") },
                    selected = false,
                    onClick = onToggleClick,
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors()
                )
                val currentRoute = navController.currentDestination?.route
                items.forEach { item ->
                    val isSelected = item.route == currentRoute
                    AddItem(
                        screen = item,
                        isSelected = isSelected,
                        navController = navController
                    )
                }


            }
        }

        // Show expand icon when the bottom nav is not expanded
        AnimatedVisibility(
            visible = !isBottomNavExpanded,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.padding(16.dp) // Adjust as needed
        ) {
            IconButton(onClick = onToggleClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "Expand")

            }
        }
    }
}



@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    navController: NavController, // Pass NavController as a parameter
    isSelected: Boolean
) {
    NavigationBarItem(
        // Text that shows bellow the icon
        label = {
            Text(text = screen.title)
        },

        // The icon resource
        icon = {
            Icon(
                painterResource(id = screen.icon),
                contentDescription = screen.title
            )
        },

        // Display if the icon it is select or not
        selected = true,

        // Always show the label bellow the icon or not
        alwaysShowLabel = true,

        // Click listener for the icon
        onClick = {
            navController.navigate(screen.route) {
                // Configure the navigation action as needed, for example:
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when navigating to a previously selected item
                restoreState = true
            }
        },

        // Control all the colors of the icon
        colors = NavigationBarItemDefaults.colors()
    )
}