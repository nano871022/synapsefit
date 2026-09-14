package co.japl.android.synapsefit.app.controller.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.DatabaseMetadata
import co.japl.android.synapsefit.core.usecase.GetDatabaseSummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DatabaseExplorerUiState {
    object Loading : DatabaseExplorerUiState

    data class Success(
        val metadata: DatabaseMetadata,
    ) : DatabaseExplorerUiState

    data class Error(
        val message: String,
    ) : DatabaseExplorerUiState
}

class DatabaseExplorerViewModel(
    private val getDatabaseSummaryUseCase: GetDatabaseSummaryUseCase? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DatabaseExplorerUiState>(DatabaseExplorerUiState.Loading)
    val uiState: StateFlow<DatabaseExplorerUiState> = _uiState.asStateFlow()

    init {
        loadDatabaseSummary()
    }

    fun loadDatabaseSummary() {
        viewModelScope.launch {
            _uiState.value = DatabaseExplorerUiState.Loading
            if (getDatabaseSummaryUseCase != null) {
                val result = getDatabaseSummaryUseCase.execute()
                result.fold(
                    onSuccess = { metadata ->
                        _uiState.value = DatabaseExplorerUiState.Success(metadata)
                    },
                    onFailure = { err ->
                        _uiState.value =
                            DatabaseExplorerUiState.Error(
                                message = err.message ?: "Error al obtener la información de la base de datos",
                            )
                    },
                )
            } else {
                _uiState.value = DatabaseExplorerUiState.Error("Servicio de base de datos no disponible")
            }
        }
    }
}
