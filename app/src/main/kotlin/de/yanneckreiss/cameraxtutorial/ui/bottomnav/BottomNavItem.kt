package de.yanneckreiss.cameraxtutorial.ui.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.ui.graphics.vector.ImageVector
import de.yanneckreiss.cameraxtutorial.R

sealed class BottomNavItem(
    val route: String,
    var title: String,
    var icon: Int
) {
    object Home :
        BottomNavItem(
            "Home",
            "Home",
            R.drawable.baseline_home_24
        )

    object Building :
        BottomNavItem(
            "Building",
            "Building",
            R.drawable.baseline_list_24
        )

    object Analytics :
        BottomNavItem(
            "Analytics",
            "Analytics",
            R.drawable.baseline_access_time_24
        )

    object Profile :
        BottomNavItem(
            "Profile",
            "Profile",
            R.drawable.baseline_person_24
        )
}