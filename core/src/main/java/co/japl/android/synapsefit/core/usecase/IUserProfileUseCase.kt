package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation
import co.japl.android.synapsefit.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface IUserProfileUseCase {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile)
    suspend fun evaluateMedicalConditions(profile: UserProfile): Result<MedicalRecommendation?>
    fun getMedicalRecommendations(): Flow<List<MedicalRecommendation>>
}
