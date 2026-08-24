package co.japl.android.synapsefit.services.llm

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import java.util.UUID

@Suppress("TooGenericExceptionCaught")
class MultiLlmClientAdapter : LlmClientPort {
    override suspend fun generateWorkoutPlan(
        promptContext: String,
        environment: TrainingEnvironment,
        gymChainQuery: String?,
        config: LlmConfig,
    ): Result<Pair<WorkoutPlan, List<Exercise>>> {
        return try {
            if (config.apiKeyEncrypted.isBlank()) {
                return Result.failure(IllegalArgumentException("Encrypted API Key is missing"))
            }

            val now = System.currentTimeMillis()
            val planId = UUID.randomUUID().toString()
            val environmentName = environment.name.lowercase().replace('_', ' ')

            val title =
                when (config.provider) {
                    LlmProvider.GEMINI -> "Gemini Plan: $environmentName"
                    LlmProvider.OPENAI -> "OpenAI Plan: $environmentName"
                    LlmProvider.ANTHROPIC -> "Anthropic Plan: $environmentName"
                }

            val gymSuffix = if (!gymChainQuery.isNullOrBlankCheck()) " (Gym: $gymChainQuery)" else ""
            val goal = "Generated for context: $promptContext$gymSuffix"

            val workoutPlan =
                WorkoutPlan(
                    id = planId,
                    title = title,
                    goalDescription = goal,
                    isActive = true,
                    generatedByLlm = true,
                    createdAt = now,
                    updatedAt = now,
                )

            val defaultExercises =
                listOf(
                    Exercise(
                        id = UUID.randomUUID().toString(),
                        planId = planId,
                        name = "Full Body Warmup",
                        muscleGroup = "FULL_BODY",
                        targetSets = 3,
                        targetReps = "12",
                        restSeconds = 60,
                        createdAt = now,
                        updatedAt = now,
                    ),
                    Exercise(
                        id = UUID.randomUUID().toString(),
                        planId = planId,
                        name = "Primary Movement ($environmentName)",
                        muscleGroup = "COMPOUND",
                        targetSets = 4,
                        targetReps = "10",
                        restSeconds = 90,
                        createdAt = now,
                        updatedAt = now,
                    ),
                )

            Result.success(Pair(workoutPlan, defaultExercises))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun String?.isNullOrBlankCheck(): Boolean = this == null || this.trim().isEmpty()
}
