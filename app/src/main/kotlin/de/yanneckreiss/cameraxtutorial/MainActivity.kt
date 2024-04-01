package de.yanneckreiss.cameraxtutorial

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.yanneckreiss.cameraxtutorial.ui.MainScreen
import de.yanneckreiss.cameraxtutorial.ui.bottomnav.BottomNavItem
import de.yanneckreiss.cameraxtutorial.ui.bottomnav.BottomNavigationT
import de.yanneckreiss.cameraxtutorial.ui.theme.JetpackComposeCameraXTutorialTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JetpackComposeCameraXTutorialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }

            }
        }
    }

    @Preview
    @Composable
    fun SimpleComposable() {
        MainScreen()
    }
}
