package co.japl.android.synapsefit.app.controller.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GoogleAuthViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() =
        runTest {
            val viewModel = GoogleAuthViewModel()
            assertEquals(GoogleAuthUiState.Idle, viewModel.uiState.value)
        }

    @Test
    fun `onGoogleLoginClicked sets state to Authenticating`() =
        runTest {
            val viewModel = GoogleAuthViewModel()
            viewModel.onGoogleLoginClicked()
            assertEquals(GoogleAuthUiState.Authenticating, viewModel.uiState.value)
        }

    @Test
    fun `onSelectAccountClicked sets state to Authenticated`() =
        runTest {
            val viewModel = GoogleAuthViewModel()
            viewModel.onSelectAccountClicked("test@gmail.com")
            val state = viewModel.uiState.value
            assert(state is GoogleAuthUiState.Authenticated)
            assertEquals("test@gmail.com", (state as GoogleAuthUiState.Authenticated).accountEmail)
        }

    @Test
    fun `onSignOutClicked resets state to Idle`() =
        runTest {
            val viewModel = GoogleAuthViewModel()
            viewModel.onSelectAccountClicked("test@gmail.com")
            viewModel.onSignOutClicked()
            assertEquals(GoogleAuthUiState.Idle, viewModel.uiState.value)
        }
}
