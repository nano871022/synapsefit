package co.japl.android.synapsefit.core.domain.model

sealed interface LlmConfigState {
    data object Ready : LlmConfigState

    data object MissingConfig : LlmConfigState

    data class MultiModelSelection(val activeConfigs: List<LlmConfig>) : LlmConfigState

    data class Error(val payload: LlmErrorPayload) : LlmConfigState
}
