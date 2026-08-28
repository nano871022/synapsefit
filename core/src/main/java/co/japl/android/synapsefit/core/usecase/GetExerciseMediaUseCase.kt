package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.firstOrNull

class GetExerciseMediaUseCase(
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort? = null,
    private val llmClientPort: LlmClientPort? = null,
) {
    suspend operator fun invoke(
        exerciseId: String,
        exerciseName: String,
        guideVideoUrl: String? = null,
        guideImageUrl: String? = null,
    ): Pair<String?, String?> {
        if (!guideVideoUrl.isNullOrBlank() || !guideImageUrl.isNullOrBlank()) {
            return Pair(guideVideoUrl, guideImageUrl)
        }

        val config = llmConfigRepositoryPort?.getActiveConfig()?.firstOrNull()
        val (videoUrl, imageUrl) =
            if (config != null && llmClientPort != null) {
                llmClientPort.fetchExerciseMedia(exerciseName, config).getOrElse {
                    fallbackMedia(exerciseName)
                }
            } else {
                fallbackMedia(exerciseName)
            }

        if (exerciseId.isNotBlank() && workoutPlanRepositoryPort != null) {
            workoutPlanRepositoryPort.updateExerciseMedia(exerciseId, videoUrl, imageUrl)
        }

        return Pair(videoUrl, imageUrl)
    }

    private fun fallbackMedia(exerciseName: String): Pair<String, String> {
        val queryFormatted = exerciseName.replace(" ", "+")
        val videoUrl = "https://www.youtube.com/results?search_query=$queryFormatted"
        val imageUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"
        return Pair(videoUrl, imageUrl)
    }
}
