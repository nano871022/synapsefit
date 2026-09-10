package co.japl.android.synapsefit.ui.viewmodel

import co.japl.android.synapsefit.core.domain.model.history.ExerciseHistory

data class WearPostWorkoutSummaryUiState(
    val planTitle: String = "",
    val day: Int = 1,
    val formattedDuration: String = "00:00",
    val totalVolumeKg: Double = 0.0,
    val averageHeartRateBpm: Int? = null,
    val maxHeartRateBpm: Int? = null,
    val exercises: List<ExerciseHistory> = emptyList(),
    val isSyncedWithPhone: Boolean = false,
    val pendingSyncDataCount: Int = 0,
    val isLoading: Boolean = false,
)
