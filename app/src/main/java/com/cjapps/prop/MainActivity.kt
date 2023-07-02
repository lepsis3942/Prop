package com.cjapps.prop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.cjapps.prop.ui.theme.PropComposeTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PropComposeTheme {
                val navController = rememberNavController()
                val navigationActions = remember(navController) {
                    PropNavigationActions(navController)
                }

                Box(Modifier.safeDrawingPadding()) {
                    PropNavGraph(
                        navController = navController,
                        navigationActions = navigationActions
                    )
                }
            }
        }
    }
}