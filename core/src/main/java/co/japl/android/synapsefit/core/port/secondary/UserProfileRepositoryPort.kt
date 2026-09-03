package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation
import co.japl.android.synapsefit.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepositoryPort {
    fun getUserProfile(): Flow<UserProfile?>

    suspend fun saveUserProfile(userProfile: UserProfile)

    suspend fun saveMedicalRecommendation(recommendation: MedicalRecommendation)

    fun getLatestMedicalRecommendation(): Flow<MedicalRecommendation?>
}
