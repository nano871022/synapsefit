package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.firstOrNull

class GenerateWorkoutPlanUseCase(
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort,
    private val llmClientPort: LlmClientPort,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
    private val bodyMeasurementRepositoryPort: BodyMeasurementRepositoryPort? = null,
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort? = null,
) {
    @Suppress("ReturnCount")
    suspend operator fun invoke(
        promptContext: String,
        environment: TrainingEnvironment,
        gymChainQuery: String? = null,
        daysPerWeek: Int? = null,
    ): Result<Pair<WorkoutPlan, List<Exercise>>> {
        val activeConfig =
            llmConfigRepositoryPort.getActiveConfig().firstOrNull()
                ?: return Result.failure(IllegalStateException("No active LLM configuration found"))

        if (environment == TrainingEnvironment.CHAIN_GYM && gymChainQuery.isNullOrBlankCheck()) {
            return Result.failure(IllegalArgumentException("Gym chain query is required for chain gym environment"))
        }

        val latestMeasurements =
            bodyMeasurementRepositoryPort?.getLatestMeasurement()?.firstOrNull()
        val recentLogs =
            workoutLogRepositoryPort?.getLogsForDateRange(0L, Long.MAX_VALUE)?.firstOrNull()?.take(5)

        val enrichedPrompt = StringBuilder(promptContext)
        daysPerWeek?.let { days ->
            enrichedPrompt.append(" | Días de entrenamiento por semana: $days")
        }
        latestMeasurements?.let { m ->
            enrichedPrompt.append(" | Medidas antropométricas actuales: Peso: ${m.weightKg}kg")
            m.chestCm?.let { enrichedPrompt.append(", Pecho: ${it}cm") }
            m.waistCm?.let { enrichedPrompt.append(", Cintura: ${it}cm") }
            m.hipCm?.let { enrichedPrompt.append(", Cadera: ${it}cm") }
        }
        if (!recentLogs.isNullOrEmpty()) {
            enrichedPrompt.append(" | Historial reciente de entrenamiento: ")
            recentLogs.forEach { log ->
                enrichedPrompt.append("[Ex: ${log.exerciseId}, ${log.repsCompleted} reps x ${log.weightLiftedKg}kg] ")
            }
        }

        val generationResult =
            llmClientPort.generateWorkoutPlan(
                promptContext = enrichedPrompt.toString(),
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
