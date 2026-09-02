package co.japl.android.synapsefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.navigation.compose.rememberNavController
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.japl.android.synapsefit.navigation.AppNavHost
import co.japl.android.synapsefit.navigation.AppNavigatorImpl
import co.japl.android.synapsefit.navigation.MainScaffold

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val dependencyContainer = DependencyContainer(this)
        setContent {
            MaterialThemeComposeUI {
                val navController = rememberNavController()
                val appNavigator = AppNavigatorImpl()
                val windowSize = calculateWindowSizeClass(this)

                MainScaffold(
                    navController = navController,
                    appNavigator = appNavigator,
                    widthSizeClass = windowSize.widthSizeClass,
                ) {
                    AppNavHost(
                        navController = navController,
                        appNavigator = appNavigator,
                        dependencyContainer = dependencyContainer,
                    )
                }
            }
        }
    }
}
