package co.japl.android.synapsefit.navigation

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AppNavigatorTest {
    @Test
    fun navigateTo_emitsToRouteCommand() =
        runTest {
            val navigator = AppNavigatorImpl()
            var command: NavigationCommand? = null
            val collectJob =
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    command = navigator.navigationCommands.first()
                }

            navigator.navigateTo(Routes.DASHBOARD)

            assert(command is NavigationCommand.ToRoute)
            assertEquals(Routes.DASHBOARD, (command as NavigationCommand.ToRoute).route)
            collectJob.cancel()
        }

    @Test
    fun navigateUp_emitsNavigateUpCommand() =
        runTest {
            val navigator = AppNavigatorImpl()
            var command: NavigationCommand? = null
            val collectJob =
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    command = navigator.navigationCommands.first()
                }

            navigator.navigateUp()

            assertEquals(NavigationCommand.NavigateUp, command)
            collectJob.cancel()
        }
}
