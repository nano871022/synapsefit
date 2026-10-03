package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.UserProfile
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserProfileUseCaseTest {

    private val userProfileRepositoryPort: UserProfileRepositoryPort = mockk(relaxed = true)
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort = mockk(relaxed = true)
    private val llmClientPort: LlmClientPort = mockk(relaxed = true)

    private lateinit var useCase: UserProfileUseCase

    @Before
    fun setUp() {
        useCase = UserProfileUseCase(
            userProfileRepository = userProfileRepositoryPort,
            llmConfigRepository = llmConfigRepositoryPort,
            llmClient = llmClientPort,
        )
    }

    @Test
    fun `getUserProfile returns profile from repository`() = runTest {
        val now = System.currentTimeMillis()
        val profile = UserProfile(
            id = "1",
            fullName = "John Doe",
            gender = "M",
            birthDate = "1990-01-01",
            heightCm = 180.0,
            bloodType = "O+",
            createdAt = now,
            updatedAt = now,
        )
        every { userProfileRepositoryPort.getUserProfile() } returns flowOf(profile)

        val result = useCase.getUserProfile().first()

        assertEquals(profile, result)
    }

    @Test
    fun `saveUserProfile delegates to repository`() = runTest {
        val now = System.currentTimeMillis()
        val profile = UserProfile(
            id = "1",
            fullName = "John Doe",
            gender = "M",
            birthDate = "1990-01-01",
            heightCm = 180.0,
            bloodType = "O+",
            createdAt = now,
            updatedAt = now,
        )

        useCase.saveUserProfile(profile)

        coVerify { userProfileRepositoryPort.saveUserProfile(any()) }
    }
}
