package co.japl.android.synapsefit.core.usecase

interface IGetExerciseMediaUseCase {
    suspend operator fun invoke(
        exerciseId: String,
        exerciseName: String,
        guideVideoUrl: String?,
        guideImageUrl: String?,
    ): Pair<String?, String?>
}
