package co.japl.android.synapsefit.ui.viewmodel

import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem

data class WearDaySelectionUiState(
    val sessions: List<WorkoutSessionItem> = emptyList(),
    val isLoading: Boolean = false,
)
