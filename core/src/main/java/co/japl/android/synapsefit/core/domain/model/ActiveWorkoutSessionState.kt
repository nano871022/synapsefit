package co.japl.android.synapsefit.core.domain.model

data class ActiveWorkoutSessionState(
    val isActive: Boolean = false,
    val activePlanDayId: String = "",
    val planId: String = "",
    val day: Int = 1,
    val sessionStartTimestamp: Long = 0L,
    val activeExerciseId: String = "",
    val exerciseStartTimestamp: Long = 0L,
)
