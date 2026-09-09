package co.japl.android.synapsefit.core.domain.model

data class ExerciseSession(
    val exerciseId: String,
    val planId: String,
    val name: String,
    val muscleGroup: String,
    val targetSets: Int,
    val targetReps: String,
    val restSeconds: Int,
    val day: Int = 1,
    val completedSets: Int = 0,
    val isCompleted: Boolean = false,
)
