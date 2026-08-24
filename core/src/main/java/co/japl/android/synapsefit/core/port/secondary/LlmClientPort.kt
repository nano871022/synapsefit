package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan

interface LlmClientPort {
    suspend fun generateWorkoutPlan(
        promptContext: String,
        environment: TrainingEnvironment,
        gymChainQuery: String?,
        config: LlmConfig
    ): Result<Pair<WorkoutPlan, List<Exercise>>>
}
