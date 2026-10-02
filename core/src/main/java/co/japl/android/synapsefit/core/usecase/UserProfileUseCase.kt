package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.LlmConfigState
import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation
import co.japl.android.synapsefit.core.domain.model.UserProfile
import co.japl.android.synapsefit.core.domain.model.parseLlmErrorResponse
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserProfileUseCase(
    private val getUserProfileUseCase: IGetUserProfileUseCase,
    private val saveUserProfileUseCase: ISaveUserProfileUseCase,
    private val evaluateMedicalConditionsUseCase: IEvaluateMedicalConditionsUseCase,
    private val getMedicalRecommendationsUseCase: IGetMedicalRecommendationsUseCase,
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort,
    private val llmClientPort: LlmClientPort,
) : IUserProfileUseCase {
    override fun getUserProfile(): Flow<UserProfile?> {
        return getUserProfileUseCase()
    }

    override fun getMedicalRecommendations(): Flow<List<MedicalRecommendation>> {
        return getMedicalRecommendationsUseCase()
    }

    override suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return saveUserProfileUseCase(profile)
    }

    override suspend fun evaluateMedicalConditions(
        gender: String,
        heightCm: Double,
        bloodType: String,
        medicalConditions: String,
    ): Result<MedicalRecommendation?> {
        return evaluateMedicalConditionsUseCase(
            gender = gender,
            heightCm = heightCm,
            bloodType = bloodType,
            medicalConditions = medicalConditions,
        )
    }

    override suspend fun checkLlmState(): LlmConfigState {
        val allConfigs = llmConfigRepositoryPort.getAllConfigs().firstOrNull() ?: emptyList()
        val activeConfigs = allConfigs.filter { it.isActive }

        return when {
            activeConfigs.isEmpty() -> LlmConfigState.MissingConfig
            activeConfigs.size > 1 -> LlmConfigState.MultiModelSelection(activeConfigs)
            else -> {
                val activeConfig = activeConfigs.first()
                val connectionResult = llmClientPort.testApiConnection(activeConfig)
                if (connectionResult.isFailure) {
                    val ex = connectionResult.exceptionOrNull()
                    LlmConfigState.Error(parseLlmErrorResponse(ex?.message ?: ex?.toString()))
                } else {
                    LlmConfigState.Ready
                }
            }
        }
    }

    override suspend fun selectActiveConfig(configId: String) {
        llmConfigRepositoryPort.setActiveConfig(configId)
    }
}
