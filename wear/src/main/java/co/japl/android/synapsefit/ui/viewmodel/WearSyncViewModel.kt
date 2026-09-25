package co.japl.android.synapsefit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.SyncStepState
import co.japl.android.synapsefit.core.usecase.ObserveWearConnectionUseCase
import co.japl.android.synapsefit.core.usecase.PerformWearSyncUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WearSyncViewModel(
    private val performWearSyncUseCase: PerformWearSyncUseCase? = null,
    observeWearConnectionUseCase: ObserveWearConnectionUseCase? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WearSyncUiState())
    val uiState: StateFlow<WearSyncUiState> = _uiState.asStateFlow()

    private var syncJob: Job? = null
    private var lastStepBeforeDisconnect: SyncStepState = SyncStepState.Idle
    private var isCancelled = false

    init {
        observeWearConnectionUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase.isPhoneConnected.collect { isConnected ->
                    val currentStep = _uiState.value.stepState
                    val isPendingStep = currentStep !is SyncStepState.Finished && currentStep !is SyncStepState.Idle
                    if (!isConnected && isPendingStep && !isCancelled) {
                        lastStepBeforeDisconnect = currentStep
                        _uiState.update {
                            it.copy(
                                isDisconnected = true,
                                stepState = SyncStepState.Disconnected(lastStepBeforeDisconnect),
                            )
                        }
                    } else if (isConnected && _uiState.value.isDisconnected && !isCancelled) {
                        _uiState.update { it.copy(isDisconnected = false) }
                        resumeSync(_uiState.value.isPostWorkout)
                    }
                }
            }
        }
    }

    fun startSync(isPostWorkout: Boolean = false) {
        if (_uiState.value.stepState !is SyncStepState.Idle && syncJob?.isActive == true) return
        isCancelled = false
        _uiState.update { it.copy(isPostWorkout = isPostWorkout, isFinished = false, isDisconnected = false) }
        runSyncFlow(isPostWorkout)
    }

    private fun resumeSync(isPostWorkout: Boolean) {
        syncJob?.cancel()
        runSyncFlow(isPostWorkout)
    }

    private fun runSyncFlow(isPostWorkout: Boolean) {
        val useCase =
            performWearSyncUseCase ?: run {
                _uiState.update { it.copy(isFinished = true, stepState = SyncStepState.Finished()) }
                return
            }

        syncJob =
            viewModelScope.launch {
                useCase(isPostWorkout).collect { step ->
                    if (isCancelled) return@collect

                    _uiState.update {
                        it.copy(
                            stepState = step,
                            isDisconnected = step is SyncStepState.Disconnected || it.isDisconnected,
                            isFinished = step is SyncStepState.Finished,
                            activeSessionDetected = (step as? SyncStepState.Finished)?.activeSessionDetected ?: false,
                            planId = (step as? SyncStepState.Finished)?.planId,
                            day = (step as? SyncStepState.Finished)?.day,
                            exerciseId = (step as? SyncStepState.Finished)?.exerciseId,
                        )
                    }
                }
            }
    }

    fun cancelSync() {
        isCancelled = true
        syncJob?.cancel()
        _uiState.update {
            it.copy(
                isDisconnected = false,
                isFinished = true,
                stepState = SyncStepState.Finished(),
            )
        }
    }
}
