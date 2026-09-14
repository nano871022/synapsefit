package co.japl.android.synapsefit.ui.viewmodel

import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem

data class WearDaySelectionUiState(
    val sessions: List<WorkoutSessionItem> = emptyList(),
    val isLoading: Boolean = false,
    val checkingUpdate: Boolean = false,
    val updateMessageResId: Int = co.japl.android.synapsefit.R.string.wear_check_update,
    val isUpdateAvailable: Boolean = false,
)
