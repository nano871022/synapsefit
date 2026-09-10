package co.japl.android.synapsefit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.usecase.GetTodayRoutineUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WearDaySelectionViewModel(
    private val getTodayRoutineUseCase: GetTodayRoutineUseCase? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WearDaySelectionUiState())
    val uiState: StateFlow<WearDaySelectionUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadSessions()
    }

    fun loadSessions() {
        val useCase = getTodayRoutineUseCase ?: return
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                useCase.invoke().collect { sessionItems ->
                    _uiState.update {
                        it.copy(
                            sessions = sessionItems,
                            isLoading = false,
                        )
                    }
                }
            }
    }
}
