package co.japl.android.synapsefit.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class NavigationCommand {
    data class ToRoute(
        val route: String,
        val popUpToRoute: String? = null,
        val inclusive: Boolean = false,
    ) : NavigationCommand()

    data object NavigateUp : NavigationCommand()
}

interface AppNavigator {
    val navigationCommands: SharedFlow<NavigationCommand>

    suspend fun navigateTo(
        route: String,
        popUpToRoute: String? = null,
        inclusive: Boolean = false,
    )

    suspend fun navigateUp()
}

class AppNavigatorImpl : AppNavigator {
    private val _navigationCommands = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = 64)
    override val navigationCommands: SharedFlow<NavigationCommand> = _navigationCommands.asSharedFlow()

    override suspend fun navigateTo(
        route: String,
        popUpToRoute: String?,
        inclusive: Boolean,
    ) {
        _navigationCommands.emit(NavigationCommand.ToRoute(route, popUpToRoute, inclusive))
    }

    override suspend fun navigateUp() {
        _navigationCommands.emit(NavigationCommand.NavigateUp)
    }
}
