package co.japl.android.synapsefit.core.domain.model

data class WorkoutSessionItem(
    val planId: String,
    val planTitle: String,
    val day: Int,
    val exerciseCount: Int,
    val isTodayScheduled: Boolean = false,
    val exercises: List<Exercise> = emptyList(),
)
