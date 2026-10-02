@file:Suppress("MaxLineLength")

package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmConfigState
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AICoachGeneratorUseCaseTest {
    private val generateWorkoutPlanUseCase: IGenerateWorkoutPlanUseCase = mockk()
    private val optimizeWorkoutPromptUseCase: IOptimizeWorkoutPromptUseCase = mockk()
    private val getExerciseMediaUseCase: IGetExerciseMediaUseCase = mockk()
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort = mockk()
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort = mockk()
    private val llmClientPort: LlmClientPort = mockk()

    private val useCase =
        AICoachGeneratorUseCase(
            generateWorkoutPlanUseCase = generateWorkoutPlanUseCase,
            optimizeWorkoutPromptUseCase = optimizeWorkoutPromptUseCase,
            getExerciseMediaUseCase = getExerciseMediaUseCase,
            workoutPlanRepositoryPort = workoutPlanRepositoryPort,
            llmConfigRepositoryPort = llmConfigRepositoryPort,
            llmClientPort = llmClientPort,
        )

    @Test
    fun `checkLlmState returns MissingConfig when no active config present`() =
        runTest {
            coEvery { llmConfigRepositoryPort.getAllConfigs() } returns flowOf(emptyList())

            val state = useCase.checkLlmState()

            assertEquals(LlmConfigState.MissingConfig, state)
        }

    @Test
    fun `checkLlmState returns MultiModelSelection when more than 1 active config`() =
        runTest {
            val configs =
                listOf(
                    LlmConfig("1", LlmProvider.GEMINI, "key1", "m1", isActive = true, 0L, 0L),
                    LlmConfig("2", LlmProvider.OPENAI, "key2", "m2", isActive = true, 0L, 0L),
                )
            coEvery { llmConfigRepositoryPort.getAllConfigs() } returns flowOf(configs)

            val state = useCase.checkLlmState()

            assertTrue(state is LlmConfigState.MultiModelSelection)
            assertEquals(2, (state as LlmConfigState.MultiModelSelection).activeConfigs.size)
        }

    @Test
    fun `checkLlmState returns Error when 1 active config fails testApiConnection`() =
        runTest {
            val config = LlmConfig("1", LlmProvider.GEMINI, "key1", "m1", isActive = true, 0L, 0L)
            coEvery { llmConfigRepositoryPort.getAllConfigs() } returns flowOf(listOf(config))
            coEvery { llmClientPort.testApiConnection(config) } returns
                Result.failure(
                    IllegalStateException("""{"code":501,"status":"NOT_IMPLEMENTED","message":"Provider unavailable"}"""),
                )

            val state = useCase.checkLlmState()

            assertTrue(state is LlmConfigState.Error)
            val err = (state as LlmConfigState.Error).payload
            assertEquals("501", err.code)
            assertEquals("NOT_IMPLEMENTED", err.status)
        }

    @Test
    fun `checkLlmState returns Ready when single active config passes testApiConnection`() =
        runTest {
            val config = LlmConfig("1", LlmProvider.GEMINI, "key1", "m1", isActive = true, 0L, 0L)
            coEvery { llmConfigRepositoryPort.getAllConfigs() } returns flowOf(listOf(config))
            coEvery { llmClientPort.testApiConnection(config) } returns Result.success(true)

            val state = useCase.checkLlmState()

            assertEquals(LlmConfigState.Ready, state)
        }

    @Test
    fun `selectActiveConfig delegates to repo`() =
        runTest {
            coEvery { llmConfigRepositoryPort.setActiveConfig("cfg1") } returns Unit

            useCase.selectActiveConfig("cfg1")

            coVerify { llmConfigRepositoryPort.setActiveConfig("cfg1") }
        }
}
