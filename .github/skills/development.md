# Skill: Jetpack Compose & UI Development ("Kinetic Pulse")

> **File Location:** `.github/skills/compose-ui.md`  
> **Target Scope:** UI, Navigation, and Design System implementation across `:ui`, `:app`, and `:wear` modules in `co.japl.android.synapsefit`.

---

## 1. Core UI Rules & Passive (Dumb) UI Contract

1. **Strictly Passive Composables:** Screens and UI components MUST NOT contain business logic, perform DB queries, or trigger direct network API calls.
2. **State Hoisting:** Composables accept an immutable `UiState` data class and emit user events as lambda callbacks (e.g., `onSaveClick: () -> Unit`).
3. **ViewModel Isolation:** Composables must NEVER instantiate or acquire ViewModels directly inside screen parameters. ViewModels are injected at the root navigation destination level.
4. **Decoupled Navigation:** Composables must NEVER take `NavController` as a parameter. All routing events call ViewModel methods that communicate with `AppNavigator`.

---

## 2. Design Tokens ("Kinetic Pulse")

### Color System (`:ui`)

```kotlin
// File: :ui/src/main/java/co/japl/android/synapsefit/ui/theme/Color.kt
package co.japl.android.synapsefit.ui.theme

import androidx.compose.ui.graphics.Color

val BackgroundDark = Color(0xFF101416)
val SurfaceContainerLow = Color(0xFF181C1E)
val SurfaceContainer = Color(0xFF1C2023)
val SurfaceContainerHigh = Color(0xFF272A2D)
val SurfaceContainerHighest = Color(0xFF323538)

val PrimaryCyan = Color(0xFF00F5FF)
val PrimaryFixedDim = Color(0xFF00DCE5)
val OnPrimaryDark = Color(0xFF003739)

val MetricCardBackground = Color(0xFFFAF9FF)
val OnMetricCardText = Color(0xFF2E3035)

val OutlineVariantDark = Color(0xFF3A494A)
val ErrorContainerDark = Color(0xFF93000A)

Typography Scale
 * Headlines (Hanken Grotesk): Metric values, total volumes, active timers.
 * Body & Titles (Inter): General labels, configuration options, exercise lists.
 * Technical Labels (JetBrains Mono): Timestamps, SHA-256 hashes, sensor units (BPM, kg, cm), sync indicators.
3. Decoupled Navigation Pattern (AppNavigator)
Interface & Navigation Host Integration
// File: :app/src/main/java/co/japl/android/synapsefit/app/navigation/AppNavHost.kt
package co.japl.android.synapsefit.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.japl.android.synapsefit.core.navigation.AppNavigator
import co.japl.android.synapsefit.core.navigation.NavigationCommand
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavHost(
    navController: NavHostController,
    navigator: AppNavigator
) {
    LaunchedEffect(Unit) {
        navigator.navigationCommands.collectLatest { command ->
            when (command) {
                is NavigationCommand.ToRoute -> {
                    navController.navigate(command.route) {
                        command.popUpToRoute?.let { popRoute ->
                            popUpTo(popRoute) { inclusive = command.inclusive }
                        }
                    }
                }
                is NavigationCommand.NavigateUp -> navController.navigateUp()
            }
        }
    }

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardRoute() }
        composable("measurements/entry") { BodyMeasurementsRoute() }
        composable("workout/plans") { WorkoutPlansRoute() }
        composable("settings/llm") { LlmSettingsRoute() }
    }
}

4. Screen Implementation Template
// File: :app/src/main/java/co/japl/android/synapsefit/app/ui/dashboard/DashboardScreen.kt
package co.japl.android.synapsefit.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.japl.android.synapsefit.ui.theme.SynapseFitTheme

data class DashboardUiState(
    val userName: String = "",
    val latestWeightKg: Double? = null,
    val isSyncing: Boolean = false
)

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onStartWorkoutClick: () -> Unit,
    onLogMeasurementClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(MaterialTheme.spacing.marginEdge)
    ) {
        Text(
            text = "Hello, ${state.userName}!",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Passive module cards go here...
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    SynapseFitTheme {
        DashboardScreen(
            state = DashboardUiState(userName = "Alex", latestWeightKg = 75.4),
            onStartWorkoutClick = {},
            onLogMeasurementClick = {}
        )
    }
}

5. Adaptive Responsive Rules (WindowSizeClass)
 * Compact Width (<600dp):
   * Display 1-column vertical layouts.
   * Bottom Navigation Bar active (BottomNavBar).
 * Medium Width (600dp – 840dp - Foldables Unfolded):
   * Use ListDetailPaneScaffold or SupportingPaneScaffold.
   * Display 2-column grid layout with persistent Navigation Rail.
 * Expanded Width (>840dp - Tablets):
   * Multi-column Bento Grid dashboard.
   * Persistent ModalNavigationDrawer expanded by default.

