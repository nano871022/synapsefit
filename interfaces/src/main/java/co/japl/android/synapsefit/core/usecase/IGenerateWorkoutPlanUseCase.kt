package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.EquipmentPreference
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.TrainingLocation
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan

interface IGenerateWorkoutPlanUseCase {
    suspend operator fun invoke(
        promptContext: String,
        location: TrainingLocation,
        equipment: EquipmentPreference,
        gymChainQuery: String? = null,
        daysPerWeek: Int? = null,
    ): Result<Pair<WorkoutPlan, List<Exercise>>>
}
