@file:Suppress("MaxLineLength", "MagicNumber", "LongMethod")

package co.japl.android.synapsefit.app.controller.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailGroup
import co.japl.android.synapsefit.core.usecase.GetWorkoutDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutDetailUiState(
    val detailGroup: WorkoutDetailGroup? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class WorkoutDetailViewModel(
    private val getWorkoutDetailUseCase: GetWorkoutDetailUseCase? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutDetailUiState())
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    fun loadWorkoutDetail(
        date: String,
        day: Int,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val detailFlow = getWorkoutDetailUseCase?.invoke(date, day) ?: flowOf(null)
            detailFlow.collect { group ->
                _uiState.update {
                    it.copy(
                        detailGroup = group,
                        isLoading = false,
                        errorMessage = if (group == null) "No se encontraron detalles para este entrenamiento" else null,
                    )
                }
            }
        }
    }
}
