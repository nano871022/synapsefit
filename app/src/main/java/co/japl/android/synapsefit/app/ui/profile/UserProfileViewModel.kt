package co.japl.android.synapsefit.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.UserProfile
import co.japl.android.synapsefit.core.usecase.GetUserProfileUseCase
import co.japl.android.synapsefit.core.usecase.SaveUserProfileUseCase
import co.japl.android.synapsefit.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserProfileUiState(
    val fullName: String = "",
    val birthDate: String = "",
    val gender: String = "HOMBRE",
    val heightCm: String = "",
    val bloodType: String = "",
    val medicalConditions: String = "",
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null,
)

class UserProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase? = null,
    private val saveUserProfileUseCase: SaveUserProfileUseCase? = null,
    private val appNavigator: AppNavigator? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        if (getUserProfileUseCase == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getUserProfileUseCase().collect { profile ->
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            fullName = profile.fullName,
                            birthDate = profile.birthDate,
                            gender = profile.gender,
                            heightCm = if (profile.heightCm > 0) profile.heightCm.toString() else "",
                            bloodType = profile.bloodType,
                            medicalConditions = profile.medicalConditions ?: "",
                            isLoading = false,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
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
            _uiState.update { it.copy(isLoading = true) }
            appNavigator?.setLoading(true)

            val profile =
                UserProfile(
                    fullName = name,
                    birthDate = state.birthDate.trim(),
                    gender = state.gender,
                    heightCm = height,
                    bloodType = state.bloodType.trim(),
                    medicalConditions = state.medicalConditions.trim().ifEmpty { null },
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                )

            val result = saveUserProfileUseCase?.invoke(profile)
            if (result == null || result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSavedSuccess = true, errorMessage = null) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar perfil",
                    )
                }
            }
            appNavigator?.setLoading(false)
        }
    }
}
