package co.japl.android.synapsefit.app.controller.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AboutDeveloperUiState(
    val versionName: String = "1.0.0",
    val versionCode: Long = 1L,
    val applicationId: String = "co.japl.android.synapsefit",
)

class AboutDeveloperViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AboutDeveloperUiState())
    val uiState: StateFlow<AboutDeveloperUiState> = _uiState.asStateFlow()
}
