package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.EquipmentPreference
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LlmConfigState
import co.japl.android.synapsefit.core.domain.model.TrainingLocation
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan

interface IAICoachGeneratorUseCase {
    suspend fun checkLlmState(): LlmConfigState

    suspend fun selectActiveConfig(configId: String)

    suspend fun generatePlan(
        promptContext: String,
        location: TrainingLocation,
        equipment: EquipmentPreference,
        gymChainQuery: String? = null,
        daysPerWeek: Int? = null,
    ): Result<Pair<WorkoutPlan, List<Exercise>>>

    suspend fun optimizePrompt(
        userPrompt: String,
        location: TrainingLocation,
        equipment: EquipmentPreference,
    ): Result<String>

    suspend fun fetchExerciseMedia(
        exerciseId: String,
        exerciseName: String,
        guideVideoUrl: String?,
        guideImageUrl: String?,
    ): Pair<String, String>

    suspend fun setActivePlan(planId: String)

    suspend fun deletePlan(planId: String)
}
