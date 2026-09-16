package co.japl.android.synapsefit.core.domain.model

sealed interface LiveSyncEvent {
    data class PingSession(
        val sourceDevice: String = "MOBILE",
    ) : LiveSyncEvent

    data class ActiveSessionStatePayload(
        val isLiveActive: Boolean,
        val planId: String,
        val day: Int = 1,
        val currentExerciseId: String = "",
        val activeSet: Int = 1,
        val cooldownTargetTimestamp: Long? = null,
        val completedExerciseIds: List<String> = emptyList(),
    ) : LiveSyncEvent

    data class StartSession(
        val planId: String,
        val day: Int = 1,
        val sessionStartTimestamp: Long = 0L,
    ) : LiveSyncEvent

    data class SelectExercise(
        val exerciseId: String,
        val exerciseName: String = "",
    ) : LiveSyncEvent

    data class CompleteSet(
        val exerciseId: String,
        val setIndex: Int,
        val reps: Int = 0,
        val weightKg: Double = 0.0,
        val targetTimestamp: Long? = null,
        val cooldownDurationSeconds: Int? = null,
    ) : LiveSyncEvent

    data class SkipExercise(
        val exerciseId: String,
    ) : LiveSyncEvent

    data class FinishSession(
        val planId: String,
        val totalDurationSeconds: Long = 0L,
    ) : LiveSyncEvent
}
