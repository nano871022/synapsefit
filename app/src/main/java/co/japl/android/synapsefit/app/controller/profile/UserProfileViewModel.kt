@file:Suppress("MaxLineLength", "LongParameterList", "TooManyFunctions", "LongMethod")

package co.japl.android.synapsefit.app.controller.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.LlmConfigState
import co.japl.android.synapsefit.core.domain.model.MedicalRecommendation
import co.japl.android.synapsefit.core.domain.model.UserProfile
import co.japl.android.synapsefit.core.usecase.IUserProfileUseCase
import co.japl.android.synapsefit.navigation.AppNavigator
import co.japl.android.synapsefit.navigation.Routes
import co.japl.android.synapsefit.service.SynapseFitForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserProfileUiState(
    val fullName: String = "",
    val birthDate: String = "",
    val gender: String = "MALE",
    val heightCm: String = "",
    val bloodType: String = "",
    val medicalConditions: String = "",
    val isLoading: Boolean = false,
    val showMedicalDialog: Boolean = false,
    val isEvaluatingMedical: Boolean = false,
    val medicalEvaluationFailed: Boolean = false,
    val medicalEvaluationError: String? = null,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null,
    val needsMedicalEvaluation: Boolean = false,
    val latestRecommendation: String? = null,
    val allRecommendations: List<MedicalRecommendation> = emptyList(),
    val llmConfigState: LlmConfigState = LlmConfigState.Ready,
)

class UserProfileViewModel(
    private val userProfileUseCase: IUserProfileUseCase? = null,
    private val appNavigator: AppNavigator? = null,
    private val context: Context? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun checkLlmState() {
        viewModelScope.launch {
            if (userProfileUseCase != null) {
                val state = userProfileUseCase.checkLlmState()
                _uiState.update { it.copy(llmConfigState = state) }
            }
        }
    }

    fun dismissLlmDialog() {
        _uiState.update { it.copy(llmConfigState = LlmConfigState.Ready) }
    }

    fun selectActiveLlmConfig(configId: String) {
        viewModelScope.launch {
            userProfileUseCase?.selectActiveConfig(configId)
            checkLlmState()
        }
    }

    fun navigateToLlmSettings() {
        dismissLlmDialog()
        viewModelScope.launch {
            appNavigator?.navigateTo(Routes.settingsLlm(openForm = true))
        }
    }

    fun loadProfile() {
        if (userProfileUseCase == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userProfileUseCase.getUserProfile().collect { profile ->
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            fullName = profile.fullName,
                            birthDate = profile.birthDate,
                            gender = profile.gender,
                            heightCm = if (profile.heightCm > 0) profile.heightCm.toString() else "",
                            bloodType = profile.bloodType,
                            medicalConditions = profile.medicalConditions ?: "",
                            needsMedicalEvaluation = profile.needsMedicalEvaluation,
                            isLoading = false,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }

        viewModelScope.launch {
            userProfileUseCase.getMedicalRecommendations().collect { list ->
                _uiState.update {
                    it.copy(
                        allRecommendations = list,
                        latestRecommendation = list.firstOrNull()?.result,
                    )
                }
            }
        }
    }

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, errorMessage = null, isSavedSuccess = false) }
    }

    fun onBirthDateChange(value: String) {
        _uiState.update { it.copy(birthDate = value, errorMessage = null, isSavedSuccess = false) }
    }

    fun onGenderChange(value: String) {
        _uiState.update { it.copy(gender = value, errorMessage = null, isSavedSuccess = false) }
    }

    fun onHeightCmChange(value: String) {
        _uiState.update { it.copy(heightCm = value, errorMessage = null, isSavedSuccess = false) }
    }

    fun onBloodTypeChange(value: String) {
        _uiState.update { it.copy(bloodType = value, errorMessage = null, isSavedSuccess = false) }
    }

    fun onMedicalConditionsChange(value: String) {
        _uiState.update { it.copy(medicalConditions = value, errorMessage = null, isSavedSuccess = false) }
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod")
    fun saveProfile() {
        val state = _uiState.value
        val name = state.fullName.trim()
        val height = state.heightCm.toDoubleOrNull() ?: 0.0

        if (name.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "El nombre es obligatorio") }
            return
        }

        if (height <= 0) {
            _uiState.update { it.copy(errorMessage = "La altura debe ser mayor a 0") }
            return
        }

        viewModelScope.launch {
            val hasMedicalConditions = state.medicalConditions.trim().isNotBlank()
            appNavigator?.setLoading(true)

            if (hasMedicalConditions && userProfileUseCase != null) {
                val llmStatus = userProfileUseCase.checkLlmState()
                if (llmStatus !is LlmConfigState.Ready) {
                    _uiState.update { it.copy(llmConfigState = llmStatus) }
                    appNavigator?.setLoading(false)
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = true,
                        isEvaluatingMedical = true,
                        showMedicalDialog = true,
                        medicalEvaluationFailed = false,
                        medicalEvaluationError = null,
                    )
                }
                context?.let { SynapseFitForegroundService.startLlmService(it, "Evaluando perfil médico") }

                val evalResult =
                    userProfileUseCase.evaluateMedicalConditions(
                        gender = state.gender,
                        heightCm = height,
                        bloodType = state.bloodType.trim(),
                        medicalConditions = state.medicalConditions.trim(),
                    )

                val recommendation = evalResult.getOrNull()
                context?.let { SynapseFitForegroundService.stopService(it) }

                if (evalResult.isFailure || recommendation == null) {
                    val errorMsg = evalResult.exceptionOrNull()?.message
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isEvaluatingMedical = false,
                            medicalEvaluationFailed = true,
                            medicalEvaluationError = errorMsg,
                        )
                    }
                    saveProfileInternal(name, height, state, needsEval = true)
                    appNavigator?.setLoading(false)
                    return@launch
                } else {
                    _uiState.update {
                        it.copy(
                            isEvaluatingMedical = false,
                            showMedicalDialog = false,
                            medicalEvaluationFailed = false,
                        )
                    }
                }
            }

            saveProfileInternal(name, height, state, needsEval = false)
            appNavigator?.setLoading(false)
        }
    }

    fun retryMedicalEvaluation() {
        val state = _uiState.value
        val name = state.fullName.trim()
        val height = state.heightCm.toDoubleOrNull() ?: 0.0
        if (state.medicalConditions.trim().isBlank() || height <= 0 || name.isEmpty()) return

        viewModelScope.launch {
            val llmStatus = userProfileUseCase?.checkLlmState() ?: LlmConfigState.Ready
            if (llmStatus !is LlmConfigState.Ready) {
                _uiState.update { it.copy(llmConfigState = llmStatus) }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    isEvaluatingMedical = true,
                    medicalEvaluationFailed = false,
                    medicalEvaluationError = null,
                )
            }
            appNavigator?.setLoading(true)
            context?.let { SynapseFitForegroundService.startLlmService(it, "Evaluando perfil médico") }

            val evalResult =
                userProfileUseCase?.evaluateMedicalConditions(
                    gender = state.gender,
                    heightCm = height,
                    bloodType = state.bloodType.trim(),
                    medicalConditions = state.medicalConditions.trim(),
                )

            val recommendation = evalResult?.getOrNull()
            context?.let { SynapseFitForegroundService.stopService(it) }

            if (evalResult == null || evalResult.isFailure || recommendation == null) {
                val errorMsg = evalResult?.exceptionOrNull()?.message
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEvaluatingMedical = false,
                        medicalEvaluationFailed = true,
                        medicalEvaluationError = errorMsg,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isEvaluatingMedical = false,
                        showMedicalDialog = false,
                        medicalEvaluationFailed = false,
                    )
                }
                saveProfileInternal(name, height, state)
            }
            appNavigator?.setLoading(false)
        }
    }

    fun dismissMedicalDialog() {
        _uiState.update {
            it.copy(
                showMedicalDialog = false,
                isEvaluatingMedical = false,
                medicalEvaluationFailed = false,
            )
        }
    }

    fun recalculateMedicalEvaluation() {
        saveProfile()
    }

    private suspend fun saveProfileInternal(
        name: String,
        height: Double,
        state: UserProfileUiState,
        needsEval: Boolean = false,
    ) {
        val profile =
            UserProfile(
                fullName = name,
                birthDate = state.birthDate.trim(),
                gender = state.gender,
                heightCm = height,
                bloodType = state.bloodType.trim(),
                medicalConditions = state.medicalConditions.trim().ifEmpty { null },
                needsMedicalEvaluation = needsEval,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )

        val result = userProfileUseCase?.saveUserProfile(profile)
        if (result == null || result.isSuccess) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSavedSuccess = true,
                    errorMessage = null,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message,
                )
            }
        }
    }
}
