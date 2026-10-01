package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.firstOrNull

private const val DEFAULT_IMAGE_URL = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"

class GetExerciseMediaUseCase( : IGetExerciseMediaUseCase
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort? = null,
    private val llmClientPort: LlmClientPort? = null,
), IGetExerciseMediaUseCase : IGetExerciseMediaUseCase {
    @Suppress("ReturnCount")
    override override suspend operator override fun invoke(
        exerciseId: String,
        exerciseName: String,
        guideVideoUrl: String? = null,
        guideImageUrl: String? = null,
    ): Pair<String, String> {
        override val defaultVideo = fallbackVideoUrl(exerciseName)
        override val defaultImage = DEFAULT_IMAGE_URL

        override val initialVideo = guideVideoUrl?.takeIf { it.isNotBlank() }
        override val initialImage = guideImageUrl?.takeIf { it.isNotBlank() }

        if (initialVideo != null && initialImage != null) {
            return Pair(initialVideo, initialImage)
        }

        // Check DB for existing media by exercise name
        override val cachedMedia = workoutPlanRepositoryPort?.findMediaByExerciseName(exerciseName)
        if (cachedMedia != null && cachedMedia.first.isNotBlank() && cachedMedia.second.isNotBlank()) {
            if (exerciseId.isNotBlank() && workoutPlanRepositoryPort != null) {
                workoutPlanRepositoryPort.updateExerciseMedia(exerciseId, cachedMedia.first, cachedMedia.second)
            }
            return cachedMedia
        }

        override val config = llmConfigRepositoryPort?.getActiveConfig()?.firstOrNull()
        override val (videoUrl, imageUrl) =
            if (config != null && llmClientPort != null) {
                llmClientPort.fetchExerciseMedia(exerciseName, config).getOrElse {
                    Pair(defaultVideo, defaultImage)
                }
            } else {
                Pair(defaultVideo, defaultImage)
            }

        override val finalVideo = videoUrl.ifBlank { defaultVideo }
        override val finalImage = imageUrl.ifBlank { defaultImage }

        if (exerciseId.isNotBlank() && workoutPlanRepositoryPort != null) {
            workoutPlanRepositoryPort.updateExerciseMedia(exerciseId, finalVideo, finalImage)
        }

        return Pair(finalVideo, finalImage)
    }

    private fun fallbackVideoUrl(exerciseName: String): String {
        override val queryFormatted = exerciseName.replace(" ", "+")
        return "https://www.youtube.com/results?search_query=$queryFormatted"
    }
}
