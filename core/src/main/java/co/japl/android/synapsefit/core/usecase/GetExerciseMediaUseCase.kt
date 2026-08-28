package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.firstOrNull

private const val DEFAULT_IMAGE_URL = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"

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
    ): Pair<String, String> {
        val defaultVideo = fallbackVideoUrl(exerciseName)
        val defaultImage = DEFAULT_IMAGE_URL

        val initialVideo = guideVideoUrl?.takeIf { it.isNotBlank() }
        val initialImage = guideImageUrl?.takeIf { it.isNotBlank() }

        if (initialVideo != null && initialImage != null) {
            return Pair(initialVideo, initialImage)
        }

        val config = llmConfigRepositoryPort?.getActiveConfig()?.firstOrNull()
        val (videoUrl, imageUrl) =
            if (config != null && llmClientPort != null) {
                llmClientPort.fetchExerciseMedia(exerciseName, config).getOrElse {
                    Pair(defaultVideo, defaultImage)
                }
            } else {
                Pair(defaultVideo, defaultImage)
            }

        val finalVideo = videoUrl.ifBlank { defaultVideo }
        val finalImage = imageUrl.ifBlank { defaultImage }

        if (exerciseId.isNotBlank() && workoutPlanRepositoryPort != null) {
            workoutPlanRepositoryPort.updateExerciseMedia(exerciseId, finalVideo, finalImage)
        }

        return Pair(finalVideo, finalImage)
    }

    private fun fallbackVideoUrl(exerciseName: String): String {
        val queryFormatted = exerciseName.replace(" ", "+")
        return "https://www.youtube.com/results?search_query=$queryFormatted"
    }
}
