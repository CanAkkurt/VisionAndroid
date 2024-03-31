@file:OptIn(ExperimentalPermissionsApi::class)

package de.yanneckreiss.cameraxtutorial.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import de.yanneckreiss.cameraxtutorial.App
import de.yanneckreiss.cameraxtutorial.R
import de.yanneckreiss.cameraxtutorial.ui.bottomnav.BottomNavItem
import de.yanneckreiss.cameraxtutorial.ui.bottomnav.BottomNavigationT
import de.yanneckreiss.cameraxtutorial.ui.features.camera.no_permission.NoPermissionScreen
import de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture.CameraScreen



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen() {

    val navController = rememberNavController()
    var isBottomNavExpanded by remember { mutableStateOf(true) }
    Scaffold(
        bottomBar = { BottomNavigationT(navController, isBottomNavExpanded, onToggleClick = { isBottomNavExpanded = !isBottomNavExpanded }) }
    ) {
        NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
            composable(route = BottomNavItem.Home.route) {
                val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
                MainContent(
                    hasPermission = cameraPermissionState.status.isGranted,
                    onRequestPermission = cameraPermissionState::launchPermissionRequest
                )

            }
            composable(route = BottomNavItem.Building.route) { /* Building screen content */ }
            composable(route = BottomNavItem.Analytics.route) { AnalyticsScreen()/* Analytics screen content */ }
            composable(route = BottomNavItem.Profile.route) { /* Profile screen content */ }
            // Add composable functions for other destinations
        }
    }
//
}

@Composable
private fun MainContent(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {

    if (hasPermission) {
        CameraScreen()
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}

@Composable
fun AnalyticsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Analytics Data", style = MaterialTheme.typography.h5)
        Text("User Engagement: Up by 20%", style = MaterialTheme.typography.body1)
        Text("App Downloads: Increased by 5%", style = MaterialTheme.typography.body1)
    }
}


@Preview
@Composable
private fun Preview_MainContent() {
    MainContent(
        hasPermission = true,
        onRequestPermission = {}
    )
}





