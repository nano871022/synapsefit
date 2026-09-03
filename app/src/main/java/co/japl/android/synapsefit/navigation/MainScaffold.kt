@file:Suppress("FunctionNaming", "LongMethod", "MatchingDeclarationName", "MagicNumber")

package co.japl.android.synapsefit.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import co.com.japl.ui.theme.spacing
import co.japl.android.synapsefit.DependencyContainer
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.ui.components.NeonButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class NavigationDrawerItemModel(
    val route: String,
    val titleRes: Int,
    val icon: @Composable () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    appNavigator: AppNavigator,
    dependencyContainer: DependencyContainer? = null,
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var menuExpanded by remember { mutableStateOf(false) }
    val isLoading by appNavigator.isLoading.collectAsState()
    var showLlmStartupPrompt by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.DASHBOARD

    LaunchedEffect(dependencyContainer) {
        if (dependencyContainer != null) {
            val activeConfig = dependencyContainer.llmConfigRepository.getActiveConfig().firstOrNull()
            if (activeConfig == null) {
                showLlmStartupPrompt = true
            }
        }
    }

    LaunchedEffect(appNavigator) {
        appNavigator.navigationCommands.collectLatest { command ->
            when (command) {
                is NavigationCommand.ToRoute -> {
                    navController.navigate(command.route) {
                        command.popUpToRoute?.let { popUpRoute ->
                            popUpTo(popUpRoute) {
                                inclusive = command.inclusive
                            }
                        }
                    }
                }
                is NavigationCommand.NavigateUp -> {
                    navController.navigateUp()
                }
            }
        }
    }

    val navItems =
        remember {
            listOf(
                NavigationDrawerItemModel(Routes.DASHBOARD, R.string.nav_dashboard) {
                    Icon(Icons.Default.Dashboard, contentDescription = null)
                },
                NavigationDrawerItemModel(Routes.MEASUREMENTS_ENTRY, R.string.nav_measurements_entry) {
                    Icon(Icons.Default.MonitorWeight, contentDescription = null)
                },
                NavigationDrawerItemModel(Routes.MEASUREMENTS_PROGRESS, R.string.nav_measurements_progress) {
                    Icon(Icons.Default.BarChart, contentDescription = null)
                },
                NavigationDrawerItemModel(Routes.WORKOUT_PLANS, R.string.nav_workout_plans) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null)
                },
                NavigationDrawerItemModel(Routes.WORKOUT_HISTORY, R.string.nav_workout_history) {
                    Icon(Icons.Default.History, contentDescription = null)
                },
            )
        }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp),
                )
                navItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = item.icon,
                        label = { Text(stringResource(item.titleRes)) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                appNavigator.navigateTo(item.route)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text(stringResource(R.string.app_name)) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu_drawer))
                            }
                        },
                        actions = {
                            IconButton(onClick = { menuExpanded = !menuExpanded }) {
                                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.menu_options))
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.user_profile)) },
                                    onClick = {
                                        menuExpanded = false
                                        scope.launch { appNavigator.navigateTo(Routes.USER_PROFILE) }
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.nav_settings_backup)) },
                                    onClick = {
                                        menuExpanded = false
                                        scope.launch { appNavigator.navigateTo(Routes.SETTINGS_BACKUP) }
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.settings_llm)) },
                                    onClick = {
                                        menuExpanded = false
                                        scope.launch { appNavigator.navigateTo(Routes.SETTINGS_LLM) }
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.settings_about)) },
                                    onClick = {
                                        menuExpanded = false
                                        scope.launch { appNavigator.navigateTo(Routes.SETTINGS_ABOUT) }
                                    },
                                )
                            }
                        },
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                            ),
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().height(2.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        )
                    }
                }
            },
            bottomBar = {
                if (widthSizeClass == WindowWidthSizeClass.Compact) {
                    NavigationBar {
                        navItems.take(4).forEach { item ->
                            NavigationBarItem(
                                icon = item.icon,
                                label = { Text(stringResource(item.titleRes)) },
                                selected = currentRoute == item.route,
                                onClick = { scope.launch { appNavigator.navigateTo(item.route) } },
                            )
                        }
                    }
                }
            },
        ) { paddingValues ->
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                if (widthSizeClass != WindowWidthSizeClass.Compact) {
                    NavigationRail {
                        navItems.forEach { item ->
                            NavigationRailItem(
                                icon = item.icon,
                                label = { Text(stringResource(item.titleRes)) },
                                selected = currentRoute == item.route,
                                onClick = { scope.launch { appNavigator.navigateTo(item.route) } },
                            )
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }
    }

    if (showLlmStartupPrompt) {
        Dialog(onDismissRequest = { showLlmStartupPrompt = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.small),
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.configure_llm_required_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Text(
                        text = stringResource(R.string.configure_llm_required_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    val tutorialUrl = stringResource(R.string.llm_tutorial_video_url)
                    OutlinedButton(
                        onClick = {
                            val intent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(tutorialUrl),
                                )
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Text(
                            text = stringResource(R.string.watch_tutorial_video),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }

                    NeonButton(
                        text = stringResource(R.string.want_to_configure),
                        onClick = {
                            showLlmStartupPrompt = false
                            scope.launch {
                                appNavigator.navigateTo(Routes.settingsLlm(openForm = true))
                            }
                        },
                    )

                    TextButton(onClick = { showLlmStartupPrompt = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}
