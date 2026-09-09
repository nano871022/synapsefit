package co.japl.android.synapsefit.service

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.util.DateTimeUtils

data class ParsedWorkoutPlanPayload(
    val plan: WorkoutPlan,
    val exercises: List<Exercise>,
    val selectedDay: Int = 1,
)

object WorkoutPlanPayloadParser {
    @Suppress("LongMethod", "CyclomaticComplexMethod", "TooGenericExceptionCaught", "ReturnCount")
    fun parseJsonPayload(json: String): ParsedWorkoutPlanPayload? {
        if (json.isBlank()) return null
        return try {
            val planId = extractValue(json, "planId").ifBlank { extractValue(json, "id") }
            if (planId.isBlank()) return null

            val title = extractValue(json, "title").ifBlank { "Workout Plan" }
            val goal = extractValue(json, "goalDescription").ifBlank { "Active Training Day" }
            val day = extractValue(json, "day").toIntOrNull() ?: 1
            val now = DateTimeUtils.getCurrentTimestamp()

            val plan =
                WorkoutPlan(
                    id = planId,
                    title = title,
                    goalDescription = goal,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now,
                )

            val exercisesList = mutableListOf<Exercise>()
            val exercisesSection = extractArraySection(json, "exercises")
            if (exercisesSection.isNotBlank()) {
                val items = exercisesSection.split("},{")
                for ((idx, item) in items.withIndex()) {
                    val clean = item.removePrefix("{").removeSuffix("}")
                    val exId = extractValue(clean, "id").ifBlank { "${planId}_ex_$idx" }
                    val name = extractValue(clean, "name").ifBlank { "Exercise ${idx + 1}" }
                    val muscleGroup = extractValue(clean, "muscleGroup").ifBlank { "Full Body" }
                    val targetSets = extractValue(clean, "targetSets").toIntOrNull() ?: DEFAULT_TARGET_SETS
                    val targetReps = extractValue(clean, "targetReps").ifBlank { DEFAULT_TARGET_REPS }
                    val restSeconds = extractValue(clean, "restSeconds").toIntOrNull() ?: DEFAULT_REST_SECONDS
                    val exDay = extractValue(clean, "day").toIntOrNull() ?: day

                    exercisesList.add(
                        Exercise(
                            id = exId,
                            planId = planId,
                            name = name,
                            muscleGroup = muscleGroup,
                            targetSets = targetSets,
                            targetReps = targetReps,
                            restSeconds = restSeconds,
                            day = exDay,
                            createdAt = now,
                            updatedAt = now,
                        ),
                    )
                }
            }

            ParsedWorkoutPlanPayload(
                plan = plan,
                exercises = exercisesList,
                selectedDay = day,
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun extractValue(
        json: String,
        key: String,
    ): String {
        val pattern = "\"$key\":\\s*\"?([^\",}]+)\"?".toRegex()
        val match = pattern.find(json)
        return match?.groupValues?.get(1)?.trim() ?: ""
    }

    @Suppress("ReturnCount")
    private fun extractArraySection(
        json: String,
        key: String,
    ): String {
        val startIndex = json.indexOf("\"$key\":")
        if (startIndex == -1) return ""
        val arrayStart = json.indexOf("[", startIndex)
        val arrayEnd = json.lastIndexOf("]")
        if (arrayStart == -1 || arrayEnd == -1 || arrayEnd <= arrayStart) return ""
        return json.substring(arrayStart + 1, arrayEnd)
    }

    private const val DEFAULT_TARGET_SETS = 3
    private const val DEFAULT_TARGET_REPS = "10"
    private const val DEFAULT_REST_SECONDS = 60
}
