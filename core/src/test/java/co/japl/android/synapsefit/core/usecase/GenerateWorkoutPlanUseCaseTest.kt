package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GenerateWorkoutPlanUseCaseTest {

    private val llmConfigRepositoryPort: LlmConfigRepositoryPort = mockk()
    private val llmClientPort: LlmClientPort = mockk()
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort = mockk(relaxed = true)

    private lateinit var useCase: GenerateWorkoutPlanUseCase

    @Before
    fun setUp() {
        useCase = GenerateWorkoutPlanUseCase(
            llmConfigRepositoryPort = llmConfigRepositoryPort,
            llmClientPort = llmClientPort,
            workoutPlanRepositoryPort = workoutPlanRepositoryPort
        )
    }

    @Test
    fun `when active config is missing, return failure`() = runTest {
        every { llmConfigRepositoryPort.getActiveConfig() } returns flowOf(null)

        val result = useCase(
            promptContext = "Build muscle",
            environment = TrainingEnvironment.BODYWEIGHT
        )

        assertTrue(result.isFailure)
        assertEquals("No active LLM configuration found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when environment is CHAIN_GYM and gym query is blank, return failure`() = runTest {
        val config = LlmConfig("1", LlmProvider.GEMINI, "key", "gemini-1.5-flash", true, 0L, 0L)
        every { llmConfigRepositoryPort.getActiveConfig() } returns flowOf(config)

        val result = useCase(
            promptContext = "Hypertrophy",
            environment = TrainingEnvironment.CHAIN_GYM,
            gymChainQuery = ""
        )

        assertTrue(result.isFailure)
        assertEquals("Gym chain query is required for chain gym environment", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when inputs valid and LLM succeeds, save plan and return success`() = runTest {
        val config = LlmConfig("1", LlmProvider.GEMINI, "key", "gemini-1.5-flash", true, 0L, 0L)
        every { llmConfigRepositoryPort.getActiveConfig() } returns flowOf(config)

        val plan = WorkoutPlan("p1", "Upper Body", "Gain muscle", true, true, 1000L, 1000L)
        val exercises = listOf(
            Exercise("e1", "p1", "Pushups", "Chest", 3, "12-15", 60, null, null, 1000L, 1000L)
        )

        coEvery {
            llmClientPort.generateWorkoutPlan("Push workout", TrainingEnvironment.BODYWEIGHT, null, config)
        } returns Result.success(Pair(plan, exercises))

        val result = useCase(
            promptContext = "Push workout",
            environment = TrainingEnvironment.BODYWEIGHT
        )

        assertTrue(result.isSuccess)
        val (retrievedPlan, retrievedExercises) = result.getOrThrow()
        assertEquals("Upper Body", retrievedPlan.title)
        assertEquals(1, retrievedExercises.size)
        coVerify(exactly = 1) { workoutPlanRepositoryPort.savePlan(plan, exercises) }
    }
}
