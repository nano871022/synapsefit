package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation
import co.japl.android.synapsefit.core.domain.model.UserProfile
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import kotlinx.coroutines.flow.Flow

class UserProfileUseCase(
    private val userProfileRepository: UserProfileRepositoryPort,
    private val llmConfigRepository: LlmConfigRepositoryPort,
    private val llmClient: LlmClientPort,
) : IUserProfileUseCase {
    private val getUserProfileUseCase = GetUserProfileUseCase(userProfileRepository)
    private val saveUserProfileUseCase = SaveUserProfileUseCase(userProfileRepository)
    private val evaluateMedicalConditionsUseCase =
        EvaluateMedicalConditionsUseCase(userProfileRepository, llmConfigRepository, llmClient)
    private val getMedicalRecommendationsUseCase = GetMedicalRecommendationsUseCase(userProfileRepository)

    override fun getUserProfile(): Flow<UserProfile?> = getUserProfileUseCase()

    override suspend fun saveUserProfile(profile: UserProfile) {
        saveUserProfileUseCase(profile)
    }

    override suspend fun evaluateMedicalConditions(profile: UserProfile): Result<MedicalRecommendation?> =
        evaluateMedicalConditionsUseCase(
            gender = profile.gender,
            heightCm = profile.heightCm,
            bloodType = profile.bloodType,
            medicalConditions = profile.medicalConditions ?: "",
        )

    override fun getMedicalRecommendations(): Flow<List<MedicalRecommendation>> = getMedicalRecommendationsUseCase()
}
