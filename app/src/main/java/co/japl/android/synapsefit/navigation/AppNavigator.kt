package co.japl.android.synapsefit.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

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
    val isLoading: StateFlow<Boolean>

    suspend fun navigateTo(
        route: String,
        popUpToRoute: String? = null,
        inclusive: Boolean = false,
    )

    suspend fun navigateUp()

    fun setLoading(loading: Boolean)
}

class AppNavigatorImpl : AppNavigator {
    private val _navigationCommands = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = 64)
    override val navigationCommands: SharedFlow<NavigationCommand> = _navigationCommands.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

    override fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
}
