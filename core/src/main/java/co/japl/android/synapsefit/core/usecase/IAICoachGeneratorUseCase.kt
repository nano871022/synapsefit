package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan

interface IAICoachGeneratorUseCase {
    suspend fun generatePlan(prompt: String): Result<WorkoutPlan>
    suspend fun optimizePrompt(userPrompt: String): Result<String>
}
