package co.japl.android.synapsefit.wear.ui.viewmodel

data class WearActiveWorkoutUiState(
    val exerciseName: String = "",
    val currentHeartRateBpm: Int = 0,
    val currentReps: Int = 0,
    val isSyncedWithPhone: Boolean = true
)
