package com.cjapps.prop

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.cjapps.prop.ui.theme.PropComposeTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        setContent {
            PropComposeTheme {
                val navController = rememberNavController()
                val navigationActions = remember(navController) {
                    PropNavigationActions(navController)
                }

                PropNavGraph(
                    navController = navController,
                    navigationActions = navigationActions
                )
            }
        }
    }
}