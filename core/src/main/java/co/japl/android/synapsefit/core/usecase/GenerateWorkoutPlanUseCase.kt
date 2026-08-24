package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.firstOrNull

class GenerateWorkoutPlanUseCase(
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort,
    private val llmClientPort: LlmClientPort,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
) {
    @Suppress("ReturnCount")
    suspend operator fun invoke(
        promptContext: String,
        environment: TrainingEnvironment,
        gymChainQuery: String? = null,
    ): Result<Pair<WorkoutPlan, List<Exercise>>> {
        val activeConfig =
            llmConfigRepositoryPort.getActiveConfig().firstOrNull()
                ?: return Result.failure(IllegalStateException("No active LLM configuration found"))

        if (environment == TrainingEnvironment.CHAIN_GYM && gymChainQuery.isNullOrBlankCheck()) {
            return Result.failure(IllegalArgumentException("Gym chain query is required for chain gym environment"))
        }

        val generationResult =
            llmClientPort.generateWorkoutPlan(
                promptContext = promptContext,
                environment = environment,
                gymChainQuery = gymChainQuery,
                config = activeConfig,
            )

        return generationResult.onSuccess { (plan, exercises) ->
            workoutPlanRepositoryPort.savePlan(plan, exercises)
        }
    }

    private fun String?.isNullOrBlankCheck(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
