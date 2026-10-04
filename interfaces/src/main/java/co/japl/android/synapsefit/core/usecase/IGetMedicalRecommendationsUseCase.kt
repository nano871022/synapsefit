package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation
import kotlinx.coroutines.flow.Flow

interface IGetMedicalRecommendationsUseCase {
    fun getLatest(): Flow<MedicalRecommendation?>

    operator fun invoke(): Flow<List<MedicalRecommendation>>
}
