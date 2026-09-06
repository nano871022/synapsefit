@file:Suppress("MaxLineLength", "LongMethod")

package co.japl.android.synapsefit.app.controller.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
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
)

class SplashViewModel(
    private val userProfileRepositoryPort: UserProfileRepositoryPort? = null,
    private val bodyMeasurementRepositoryPort: BodyMeasurementRepositoryPort? = null,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort? = null,
    private val minSplashDurationMs: Long = 3000L,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        startPreloading()
    }

    fun startPreloading() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isReady = false) }

            val minTimeDeferred =
                async {
                    delay(minSplashDurationMs)
                }

            val preloadDataDeferred =
                async {
                    preloadData()
                }

            minTimeDeferred.await()
            preloadDataDeferred.await()

            _uiState.update {
                it.copy(
                    isReady = true,
                    isLoading = false,
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
