package co.japl.android.synapsefit.wear.ui

data class WearActiveWorkoutUiState(
    val exerciseName: String = "",
    val currentHeartRateBpm: Int = 0,
    val currentReps: Int = 0,
    val isSyncedWithPhone: Boolean = true
)
