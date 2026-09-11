package co.japl.android.synapsefit.app.controller.auth

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    fun `onGoogleLoginClicked sets state to Authenticating without repository`() =
        runTest {
            val viewModel = GoogleAuthViewModel()
            viewModel.onGoogleLoginClicked()
            assertEquals(GoogleAuthUiState.Authenticating, viewModel.uiState.value)
        }

    @Test
    fun `onSelectAccountClicked sets state to Authenticated without repository`() =
        runTest {
            val viewModel = GoogleAuthViewModel()
            viewModel.onSelectAccountClicked("test@gmail.com")
            val state = viewModel.uiState.value
            assertTrue(state is GoogleAuthUiState.Authenticated)
            assertEquals("test@gmail.com", (state as GoogleAuthUiState.Authenticated).accountEmail)
        }

    @Test
    fun `onSignOutClicked resets state to Idle without repository`() =
        runTest {
            val viewModel = GoogleAuthViewModel()
            viewModel.onSelectAccountClicked("test@gmail.com")
            viewModel.onSignOutClicked()
            assertEquals(GoogleAuthUiState.Idle, viewModel.uiState.value)
        }

    @Test
    fun `observeAuthState updates uiState when repository authState changes`() =
        runTest {
            val authStateFlow = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
            val mockRepo = mockk<GoogleAuthRepository>()
            every { mockRepo.authState } returns authStateFlow

            val viewModel = GoogleAuthViewModel(mockRepo)
            assertEquals(GoogleAuthUiState.Idle, viewModel.uiState.value)

            authStateFlow.value = AuthState.Authenticated("user@gmail.com", "Test User")
            val state = viewModel.uiState.value
            assertTrue(state is GoogleAuthUiState.Authenticated)
            assertEquals("user@gmail.com", (state as GoogleAuthUiState.Authenticated).accountEmail)
            assertEquals("Test User", state.displayName)
        }

    @Test
    fun `onGoogleLoginClicked with repository success updates state to Authenticated`() =
        runTest {
            val authStateFlow = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
            val mockRepo = mockk<GoogleAuthRepository>()
            every { mockRepo.authState } returns authStateFlow
            coEvery { mockRepo.signIn(any()) } returns Result.success(AuthState.Authenticated("user@gmail.com", "User"))

            val viewModel = GoogleAuthViewModel(mockRepo)
            viewModel.onGoogleLoginClicked()

            val state = viewModel.uiState.value
            assertTrue(state is GoogleAuthUiState.Authenticated)
            assertEquals("user@gmail.com", (state as GoogleAuthUiState.Authenticated).accountEmail)
        }

    @Test
    fun `onGoogleLoginClicked with repository failure updates state to Error`() =
        runTest {
            val authStateFlow = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
            val mockRepo = mockk<GoogleAuthRepository>()
            every { mockRepo.authState } returns authStateFlow
            coEvery { mockRepo.signIn(any()) } returns Result.failure(RuntimeException("Auth error"))

            val viewModel = GoogleAuthViewModel(mockRepo)
            viewModel.onGoogleLoginClicked()

            val state = viewModel.uiState.value
            assertTrue(state is GoogleAuthUiState.Error)
            assertEquals("Auth error", (state as GoogleAuthUiState.Error).message)
        }

    @Test
    fun `onSignOutClicked with repository failure sets Error state`() =
        runTest {
            val authStateFlow = MutableStateFlow<AuthState>(AuthState.Authenticated("user@gmail.com"))
            val mockRepo = mockk<GoogleAuthRepository>()
            every { mockRepo.authState } returns authStateFlow
            coEvery { mockRepo.signOut() } returns Result.failure(RuntimeException("Sign out error"))

            val viewModel = GoogleAuthViewModel(mockRepo)
            viewModel.onSignOutClicked()

            val state = viewModel.uiState.value
            assertTrue(state is GoogleAuthUiState.Error)
            assertEquals("Sign out error", (state as GoogleAuthUiState.Error).message)
        }

    @Test
    fun `onErrorDismissed resets error state to Idle`() =
        runTest {
            val authStateFlow = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
            val mockRepo = mockk<GoogleAuthRepository>()
            every { mockRepo.authState } returns authStateFlow
            coEvery { mockRepo.signIn(any()) } returns Result.failure(RuntimeException("Error"))

            val viewModel = GoogleAuthViewModel(mockRepo)
            viewModel.onGoogleLoginClicked()
            assertTrue(viewModel.uiState.value is GoogleAuthUiState.Error)

            viewModel.onErrorDismissed()
            assertEquals(GoogleAuthUiState.Idle, viewModel.uiState.value)
        }
}
