package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.EquipmentPreference
import co.japl.android.synapsefit.core.domain.model.TrainingLocation
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import kotlinx.coroutines.flow.firstOrNull

class OptimizeWorkoutPromptUseCase(
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort,
    private val llmClientPort: LlmClientPort,
) {
    suspend operator fun invoke(
        userPrompt: String,
        location: TrainingLocation,
        equipment: EquipmentPreference,
    ): Result<String> {
        val activeConfig =
            llmConfigRepositoryPort.getActiveConfig().firstOrNull()
                ?: return Result.failure(IllegalStateException("No hay un proveedor LLM activo configurado"))

        return llmClientPort.optimizePrompt(
            userPrompt = userPrompt,
            location = location.name,
            equipment = equipment.name,
            config = activeConfig,
        )
    }
}
