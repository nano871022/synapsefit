package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class EvaluateMedicalConditionsUseCase(
    private val userProfileRepositoryPort: UserProfileRepositoryPort,
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort,
    private val llmClientPort: LlmClientPort,
) {
    @Suppress("ReturnCount")
    suspend operator fun invoke(
        gender: String,
        heightCm: Double,
        bloodType: String,
        medicalConditions: String,
    ): Result<MedicalRecommendation?> {
        if (medicalConditions.isBlank()) return Result.success(null)

        val activeConfig =
            llmConfigRepositoryPort.getActiveConfig().firstOrNull()
                ?: return Result.failure(IllegalStateException("No hay un proveedor LLM activo configurado"))

        val medicalResult =
            llmClientPort.generateMedicalRecommendation(
                gender = gender,
                heightCm = heightCm,
                bloodType = bloodType,
                medicalConditions = medicalConditions,
                config = activeConfig,
            )

        return medicalResult.map { text ->
            if (text.isNotBlank()) {
                val recommendation =
                    MedicalRecommendation(
                        id = UUID.randomUUID().toString(),
                        profileCode = "PRIMARY_USER",
                        result = text,
                        createdAt = System.currentTimeMillis(),
                    )
                userProfileRepositoryPort.saveMedicalRecommendation(recommendation)
                recommendation
            } else {
                null
            }
        }
    }
}
