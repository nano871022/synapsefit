@file:Suppress("TooManyFunctions", "LongMethod")

package co.japl.android.synapsefit.app.controller.measurements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.usecase.SaveBodyMeasurementUseCase
import co.japl.android.synapsefit.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BodyMeasurementsUiState(
    val weightKg: String = "",
    val chestCm: String = "",
    val waistCm: String = "",
    val hipCm: String = "",
    val bicepLeftCm: String = "",
    val bicepRightCm: String = "",
    val thighLeftCm: String = "",
    val thighRightCm: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val isPopupOpen: Boolean = false,
    val errorMessage: String? = null,
    val history: List<BodyMeasurement> = emptyList(),
)

class BodyMeasurementsViewModel(
    private val saveBodyMeasurementUseCase: SaveBodyMeasurementUseCase? = null,
    private val bodyMeasurementRepositoryPort: BodyMeasurementRepositoryPort? = null,
    private val appNavigator: AppNavigator? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BodyMeasurementsUiState())
    val uiState: StateFlow<BodyMeasurementsUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            appNavigator?.setLoading(true)
            bodyMeasurementRepositoryPort?.getMeasurementsHistory()?.collectLatest { list ->
                _uiState.update { it.copy(history = list) }
                appNavigator?.setLoading(false)
            }
        }
    }

    fun openPopup() {
        _uiState.update { it.copy(isPopupOpen = true, errorMessage = null) }
    }

    fun closePopup() {
        _uiState.update { it.copy(isPopupOpen = false, errorMessage = null) }
    }

    fun onWeightChange(value: String) {
        _uiState.update { it.copy(weightKg = value, errorMessage = null) }
    }

    fun onChestChange(value: String) {
        _uiState.update { it.copy(chestCm = value) }
    }

    fun onWaistChange(value: String) {
        _uiState.update { it.copy(waistCm = value) }
    }

    fun onHipChange(value: String) {
        _uiState.update { it.copy(hipCm = value) }
    }

    fun onBicepLeftChange(value: String) {
        _uiState.update { it.copy(bicepLeftCm = value) }
    }

    fun onBicepRightChange(value: String) {
        _uiState.update { it.copy(bicepRightCm = value) }
    }

    fun onThighLeftChange(value: String) {
        _uiState.update { it.copy(thighLeftCm = value) }
    }

    fun onThighRightChange(value: String) {
        _uiState.update { it.copy(thighRightCm = value) }
    }

    fun onNotesChange(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun saveMeasurement() {
        val state = _uiState.value
        val weight = state.weightKg.toDoubleOrNull()
        if (weight == null || weight <= 0) {
            _uiState.update { it.copy(errorMessage = "El peso es obligatorio y debe ser mayor a 0") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            appNavigator?.setLoading(true)

            if (saveBodyMeasurementUseCase != null) {
                val result =
                    saveBodyMeasurementUseCase(
                        weightKg = weight,
                        chestCm = state.chestCm.toDoubleOrNull(),
                        waistCm = state.waistCm.toDoubleOrNull(),
                        hipCm = state.hipCm.toDoubleOrNull(),
                        bicepLeftCm = state.bicepLeftCm.toDoubleOrNull(),
                        bicepRightCm = state.bicepRightCm.toDoubleOrNull(),
                        thighLeftCm = state.thighLeftCm.toDoubleOrNull(),
                        thighRightCm = state.thighRightCm.toDoubleOrNull(),
                        notes = state.notes.ifBlank { null },
                    )
                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isSavedSuccess = true,
                            isPopupOpen = false,
                            weightKg = "",
                            chestCm = "",
                            waistCm = "",
                            hipCm = "",
                            bicepLeftCm = "",
                            bicepRightCm = "",
                            thighLeftCm = "",
                            thighRightCm = "",
                            notes = "",
                        )
                    }
                    appNavigator?.setLoading(false)
                } else {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar la medida",
                        )
                    }
                    appNavigator?.setLoading(false)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSavedSuccess = true,
                        isPopupOpen = false,
                        weightKg = "",
                        chestCm = "",
                        waistCm = "",
                        hipCm = "",
                        bicepLeftCm = "",
                        bicepRightCm = "",
                        thighLeftCm = "",
                        thighRightCm = "",
                        notes = "",
                    )
                }
                appNavigator?.setLoading(false)
            }
        }
    }
}
