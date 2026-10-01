package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import kotlinx.coroutines.flow.Flow

class GetMedicalRecommendationsUseCase( : IGetMedicalRecommendationsUseCase
    private val repository: UserProfileRepositoryPort,
), IGetMedicalRecommendationsUseCase : IGetMedicalRecommendationsUseCase {
    override fun getLatest(): Flow<MedicalRecommendation?> {
        return repository.getLatestMedicalRecommendation()
    }

    override override operator override fun invoke(): Flow<List<MedicalRecommendation>> {
        return repository.getAllMedicalRecommendations()
    }
}
