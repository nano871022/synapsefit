package co.japl.android.synapsefit.navigation

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AppNavigatorTest {
    @Test
    fun navigateTo_emitsToRouteCommand() =
        runTest {
            val navigator = AppNavigatorImpl()
            navigator.navigateTo(Routes.DASHBOARD)

            val command = navigator.navigationCommands.first()
            assert(command is NavigationCommand.ToRoute)
            assertEquals(Routes.DASHBOARD, (command as NavigationCommand.ToRoute).route)
        }

    @Test
    fun navigateUp_emitsNavigateUpCommand() =
        runTest {
            val navigator = AppNavigatorImpl()
            navigator.navigateUp()

            val command = navigator.navigationCommands.first()
            assertEquals(NavigationCommand.NavigateUp, command)
        }
}
