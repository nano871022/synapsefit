package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan

interface LlmClientPort {
    suspend fun generateWorkoutPlan(
        promptContext: String,
        environment: TrainingEnvironment,
        gymChainQuery: String?,
        config: LlmConfig,
    ): Result<Pair<WorkoutPlan, List<Exercise>>>

    suspend fun fetchAvailableModels(
        provider: LlmProvider,
        apiKey: String,
    ): Result<List<String>>

    suspend fun fetchExerciseMedia(
        exerciseName: String,
        config: LlmConfig,
    ): Result<Pair<String, String>>

    suspend fun testApiConnection(config: LlmConfig): Result<Boolean>

    suspend fun generateMedicalRecommendation(
        gender: String,
        heightCm: Double,
        bloodType: String,
        medicalConditions: String,
        config: LlmConfig,
    ): Result<String>
}
