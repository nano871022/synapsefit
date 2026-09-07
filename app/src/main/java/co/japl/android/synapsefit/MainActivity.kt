package co.japl.android.synapsefit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.japl.android.synapsefit.app.controller.workout.WorkoutSessionStateManager
import co.japl.android.synapsefit.navigation.AppNavHost
import co.japl.android.synapsefit.navigation.AppNavigatorImpl
import co.japl.android.synapsefit.navigation.MainScaffold
import co.japl.android.synapsefit.navigation.Routes

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { _ -> }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        val dependencyContainer = DependencyContainer(this)
        val activeRestoredSession = WorkoutSessionStateManager.loadSession(this)
        val startDest =
            if (activeRestoredSession != null && activeRestoredSession.uiState.planId.isNotBlank()) {
                Routes.workoutActive(activeRestoredSession.uiState.planId)
            } else {
                Routes.SPLASH
            }

        setContent {
            MaterialThemeComposeUI {
                val navController = rememberNavController()
                val appNavigator = AppNavigatorImpl()
                val windowSize = calculateWindowSizeClass(this)

                MainScaffold(
                    navController = navController,
                    appNavigator = appNavigator,
                    dependencyContainer = dependencyContainer,
                    widthSizeClass = windowSize.widthSizeClass,
                ) {
                    AppNavHost(
                        navController = navController,
                        appNavigator = appNavigator,
                        dependencyContainer = dependencyContainer,
                        startDestination = startDest,
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
