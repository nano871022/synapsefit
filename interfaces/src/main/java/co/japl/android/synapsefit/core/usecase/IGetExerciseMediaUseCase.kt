package co.japl.android.synapsefit.core.usecase

interface IGetExerciseMediaUseCase {
    suspend operator fun invoke(
        exerciseId: String,
        exerciseName: String,
        guideVideoUrl: String? = null,
        guideImageUrl: String? = null,
    ): Pair<String, String>
}
