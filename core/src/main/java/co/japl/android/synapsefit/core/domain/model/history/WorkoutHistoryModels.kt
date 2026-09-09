package co.japl.android.synapsefit.core.domain.model.history

import co.japl.android.synapsefit.core.domain.model.SourceDevice

/**
 * Flat representation of a JOIN query result between WorkoutLog, Exercise, and Plan.
 */
data class WorkoutHistoryRecord(
    val logId: String,
    val exerciseId: String,
    val planId: String,
    val planTitle: String,
    val day: Int,
    val exerciseName: String,
    val muscleGroup: String,
    val repsCompleted: Int,
    val weightLiftedKg: Double,
    val heartRateBpm: Int?,
    val durationSeconds: Long,
    val sourceDevice: SourceDevice,
    val timestamp: Long,
)

/**
 * Represent a set of a specific exercise in history.
 */
data class ExerciseSetHistory(
    val setIndex: Int,
    val repsCompleted: Int,
    val weightLiftedKg: Double,
    val heartRateBpm: Int?,
    val durationSeconds: Long,
    val timestamp: Long,
)

/**
 * Represent an exercise performed in a session with all its sets.
 */
data class ExerciseHistory(
    val exerciseId: String,
    val exerciseName: String,
    val muscleGroup: String,
    val sets: List<ExerciseSetHistory>,
    val averageReps: Double,
    val averageWeightKg: Double,
)

/**
 * Represent a workout session grouped by plan and day.
 */
data class WorkoutHistoryGroup(
    val sessionId: String,
    val planId: String,
    val planTitle: String,
    val day: Int,
    val timestamp: Long,
    val exercises: List<ExerciseHistory>,
    val totalVolumeKg: Double,
    val totalDurationSeconds: Long,
    val muscleGroups: List<String>,
)
