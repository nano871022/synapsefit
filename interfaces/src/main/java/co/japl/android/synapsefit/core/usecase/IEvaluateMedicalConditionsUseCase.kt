package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation

interface IEvaluateMedicalConditionsUseCase {
    suspend operator fun invoke(
        gender: String,
        heightCm: Double,
        bloodType: String,
        medicalConditions: String,
    ): Result<MedicalRecommendation?>
}
