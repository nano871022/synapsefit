package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import kotlinx.coroutines.flow.Flow

class GetMedicalRecommendationsUseCase(
    private val repository: UserProfileRepositoryPort,
) {
    fun getLatest(): Flow<MedicalRecommendation?> {
        return repository.getLatestMedicalRecommendation()
    }

    operator fun invoke(): Flow<List<MedicalRecommendation>> {
        return repository.getAllMedicalRecommendations()
    }
}
