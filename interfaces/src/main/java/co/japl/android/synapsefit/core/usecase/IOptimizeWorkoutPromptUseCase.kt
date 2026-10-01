package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.EquipmentPreference
import co.japl.android.synapsefit.core.domain.model.TrainingLocation

interface IOptimizeWorkoutPromptUseCase {
    suspend operator fun invoke(
        userPrompt: String,
        location: TrainingLocation,
        equipment: EquipmentPreference,
    ): Result<String>
}
