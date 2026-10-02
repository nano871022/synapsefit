package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.EquipmentPreference
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LlmConfigState
import co.japl.android.synapsefit.core.domain.model.TrainingLocation
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.domain.model.parseLlmErrorResponse
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.firstOrNull

class AICoachGeneratorUseCase(
    private val generateWorkoutPlanUseCase: IGenerateWorkoutPlanUseCase,
    private val optimizeWorkoutPromptUseCase: IOptimizeWorkoutPromptUseCase,
    private val getExerciseMediaUseCase: IGetExerciseMediaUseCase,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort,
    private val llmClientPort: LlmClientPort,
) : IAICoachGeneratorUseCase {
    override suspend fun checkLlmState(): LlmConfigState {
        val allConfigs = llmConfigRepositoryPort.getAllConfigs().firstOrNull() ?: emptyList()
        val activeConfigs = allConfigs.filter { it.isActive }

        return when {
            activeConfigs.isEmpty() -> LlmConfigState.MissingConfig
            activeConfigs.size > 1 -> LlmConfigState.MultiModelSelection(activeConfigs)
            else -> {
                val activeConfig = activeConfigs.first()
                val connectionResult = llmClientPort.testApiConnection(activeConfig)
                if (connectionResult.isFailure) {
                    val ex = connectionResult.exceptionOrNull()
                    LlmConfigState.Error(parseLlmErrorResponse(ex?.message ?: ex?.toString()))
                } else {
                    LlmConfigState.Ready
                }
            }
        }
    }

    override suspend fun selectActiveConfig(configId: String) {
        llmConfigRepositoryPort.setActiveConfig(configId)
    }

    override suspend fun generatePlan(
        promptContext: String,
        location: TrainingLocation,
        equipment: EquipmentPreference,
        gymChainQuery: String?,
        daysPerWeek: Int?,
    ): Result<Pair<WorkoutPlan, List<Exercise>>> {
        return generateWorkoutPlanUseCase(
            promptContext = promptContext,
            location = location,
            equipment = equipment,
            gymChainQuery = gymChainQuery,
            daysPerWeek = daysPerWeek,
        )
    }

    override suspend fun optimizePrompt(
        userPrompt: String,
        location: TrainingLocation,
        equipment: EquipmentPreference,
    ): Result<String> {
        return optimizeWorkoutPromptUseCase(
            userPrompt = userPrompt,
            location = location,
            equipment = equipment,
        )
    }

    override suspend fun fetchExerciseMedia(
        exerciseId: String,
        exerciseName: String,
        guideVideoUrl: String?,
        guideImageUrl: String?,
    ): Pair<String, String> {
        return getExerciseMediaUseCase(
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            guideVideoUrl = guideVideoUrl,
            guideImageUrl = guideImageUrl,
        )
    }

    override suspend fun setActivePlan(planId: String) {
        workoutPlanRepositoryPort.setActivePlan(planId)
    }

    override suspend fun deletePlan(planId: String) {
        workoutPlanRepositoryPort.deletePlan(planId)
    }
}
