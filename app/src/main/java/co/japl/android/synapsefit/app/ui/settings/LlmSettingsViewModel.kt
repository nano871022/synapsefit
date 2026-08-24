@file:Suppress("MagicNumber")

package co.japl.android.synapsefit.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LlmProviderUiModel(
    val id: String,
    val provider: LlmProvider,
    val apiKeyMasked: String,
    val modelName: String,
    val isActive: Boolean,
)

data class LlmSettingsUiState(
    val providers: List<LlmProviderUiModel> = emptyList(),
    val activeProviderId: String? = null,
    val availableModels: List<String> = emptyList(),
    val isLoading: Boolean = false,
)

class LlmSettingsViewModel(
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort? = null,
    private val llmClientPort: LlmClientPort? = null,
    private val appNavigator: AppNavigator? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LlmSettingsUiState())
    val uiState: StateFlow<LlmSettingsUiState> = _uiState.asStateFlow()

    init {
        loadConfigs()
    }

    fun loadConfigs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            appNavigator?.setLoading(true)

            val configs =
                llmConfigRepositoryPort?.getAllConfigs()?.let { flow ->
                    flow.firstOrNull()
                } ?: emptyList()

            val active = configs.find { it.isActive }

            val mapped =
                configs.map { c ->
                    LlmProviderUiModel(
                        id = c.id,
                        provider = c.provider,
                        apiKeyMasked = maskApiKey(c.apiKeyEncrypted),
                        modelName = c.modelName,
                        isActive = c.isActive,
                    )
                }

            _uiState.update {
                it.copy(
                    providers = mapped,
                    activeProviderId = active?.id,
                    isLoading = false,
                )
            }
            appNavigator?.setLoading(false)
        }
    }

    fun fetchModels(
        provider: LlmProvider,
        apiKey: String,
    ) {
        if (apiKey.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            appNavigator?.setLoading(true)

            val result = llmClientPort?.fetchAvailableModels(provider, apiKey)
            result?.onSuccess { models ->
                _uiState.update { it.copy(availableModels = models, isLoading = false) }
            }?.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }

            appNavigator?.setLoading(false)
        }
    }

    fun saveConfig(
        provider: LlmProvider,
        apiKey: String,
        modelName: String,
    ) {
        viewModelScope.launch {
            appNavigator?.setLoading(true)
            val now = System.currentTimeMillis()
            val config =
                LlmConfig(
                    id = java.util.UUID.randomUUID().toString(),
                    provider = provider,
                    apiKeyEncrypted = apiKey.trim(),
                    modelName = modelName.trim(),
                    isActive = true,
                    createdAt = now,
                    updatedAt = now,
                )
            llmConfigRepositoryPort?.saveConfig(config)
            loadConfigs()
            appNavigator?.setLoading(false)
        }
    }

    private fun maskApiKey(key: String): String {
        return if (key.length > 8) {
            key.take(4) + "...." + key.takeLast(4)
        } else {
            "********"
        }
    }
}
