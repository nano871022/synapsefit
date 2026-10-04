package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GoogleAccountLoginUseCaseTest {
    private lateinit var googleAuthRepository: GoogleAuthRepository
    private lateinit var useCase: GoogleAccountLoginUseCase
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Unauthenticated)

    @Before
    fun setUp() {
        googleAuthRepository = mockk(relaxed = true)
        every { googleAuthRepository.authState } returns authStateFlow
        useCase = GoogleAccountLoginUseCase(googleAuthRepository)
    }

    @Test
    fun testAuthStateFlow() {
        assertEquals(AuthState.Unauthenticated, useCase.authState.value)
    }

    @Test
    fun testSignInSuccess() =
        runBlocking {
            val expectedState = AuthState.Authenticated("test@example.com", "Test User")
            coEvery { googleAuthRepository.signIn(any()) } returns Result.success(expectedState)

            val result = useCase.signIn("dummyContext")
            assertTrue(result.isSuccess)
            assertEquals(expectedState, result.getOrNull())
            coVerify { googleAuthRepository.signIn("dummyContext") }
        }

    @Test
    fun testSelectAccount() =
        runBlocking {
            val expectedState = AuthState.Authenticated("user@example.com")
            coEvery { googleAuthRepository.signIn("user@example.com") } returns Result.success(expectedState)

            val result = useCase.selectAccount("user@example.com")
            assertTrue(result.isSuccess)
            assertEquals(expectedState, result.getOrNull())
            coVerify { googleAuthRepository.signIn("user@example.com") }
        }

    @Test
    fun testAddAnotherAccount() =
        runBlocking {
            val expectedState = AuthState.Authenticated("new@example.com")
            coEvery { googleAuthRepository.signIn("add_another_account") } returns Result.success(expectedState)

            val result = useCase.addAnotherAccount()
            assertTrue(result.isSuccess)
            assertEquals(expectedState, result.getOrNull())
            coVerify { googleAuthRepository.signIn("add_another_account") }
        }

    @Test
    fun testSignOut() =
        runBlocking {
            coEvery { googleAuthRepository.signOut() } returns Result.success(Unit)

            val result = useCase.signOut()
            assertTrue(result.isSuccess)
            coVerify { googleAuthRepository.signOut() }
        }
}
