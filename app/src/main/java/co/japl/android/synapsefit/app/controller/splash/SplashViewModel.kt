@file:Suppress("MaxLineLength", "LongMethod")

package co.japl.android.synapsefit.app.controller.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.SyncState
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.CheckAndRestoreBackupUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SplashUiState(
    val isReady: Boolean = false,
    val isLoading: Boolean = true,
    val syncState: SyncState = SyncState.Idle,
)

class SplashViewModel(
    private val userProfileRepositoryPort: UserProfileRepositoryPort? = null,
    private val bodyMeasurementRepositoryPort: BodyMeasurementRepositoryPort? = null,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort? = null,
    private val checkAndRestoreBackupUseCase: CheckAndRestoreBackupUseCase? = null,
    private val minSplashDurationMs: Long = 3000L,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        startPreloading()
    }

    fun startPreloading() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isReady = false, syncState = SyncState.Checking) }

            val minTimeDeferred =
                async {
                    delay(minSplashDurationMs)
                }

            val syncDeferred =
                async {
                    checkAndRestoreBackupUseCase?.execute()?.fold(
                        onSuccess = { isRestored ->
                            if (isRestored) {
                                _uiState.update { it.copy(syncState = SyncState.Restoring) }
                            }
                        },
                        onFailure = { err ->
                            _uiState.update { it.copy(syncState = SyncState.Error(err.message ?: "Sync error")) }
                        },
                    )
                }

            val preloadDataDeferred =
                async {
                    preloadData()
                }

            minTimeDeferred.await()
            syncDeferred.await()
            preloadDataDeferred.await()

            _uiState.update {
                it.copy(
                    isReady = true,
                    isLoading = false,
                    syncState = SyncState.Idle,
                )
            }
        }
    }

    private suspend fun preloadData() {
        runCatching {
            userProfileRepositoryPort?.getUserProfile()?.firstOrNull()
        }
        runCatching {
            bodyMeasurementRepositoryPort?.getMeasurementsHistory()?.firstOrNull()
        }
        runCatching {
            workoutPlanRepositoryPort?.getActivePlan()?.firstOrNull()
        }
        runCatching {
            llmConfigRepositoryPort?.getActiveConfig()?.firstOrNull()
        }
    }
}
