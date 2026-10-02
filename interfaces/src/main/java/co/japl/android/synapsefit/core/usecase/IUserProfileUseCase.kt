package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.LlmConfigState
import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation
import co.japl.android.synapsefit.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface IUserProfileUseCase {
    fun getUserProfile(): Flow<UserProfile?>

    fun getMedicalRecommendations(): Flow<List<MedicalRecommendation>>

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit>

    suspend fun evaluateMedicalConditions(
        gender: String,
        heightCm: Double,
        bloodType: String,
        medicalConditions: String,
    ): Result<MedicalRecommendation?>

    suspend fun checkLlmState(): LlmConfigState

    suspend fun selectActiveConfig(configId: String)
}
