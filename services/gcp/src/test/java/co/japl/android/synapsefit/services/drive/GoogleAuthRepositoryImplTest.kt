package co.japl.android.synapsefit.services.drive

import co.japl.android.synapsefit.core.domain.model.AuthState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleAuthRepositoryImplTest {
    @Test
    fun `initial state is Unauthenticated when context is null`() =
        runTest {
            val repository = GoogleAuthRepositoryImpl()

            assertEquals(AuthState.Unauthenticated, repository.authState.value)
        }

    @Test
    fun `signIn returns failure when context is null`() =
        runTest {
            val repository = GoogleAuthRepositoryImpl()

            val result = repository.signIn(Any())

            assertTrue(result.isFailure)
            assertEquals(AuthState.Unauthenticated, repository.authState.value)
        }

    @Test
    fun `signOut resets state to Unauthenticated`() =
        runTest {
            val repository = GoogleAuthRepositoryImpl()

            val result = repository.signOut()

            assertTrue(result.isSuccess)
            assertEquals(AuthState.Unauthenticated, repository.authState.value)
        }

    @Test
    fun `getAccessToken returns failure when context is null`() =
        runTest {
            val repository = GoogleAuthRepositoryImpl()

            val result = repository.getAccessToken()

            assertTrue(result.isFailure)
        }
}
