package co.japl.android.synapsefit.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.usecase.GetTodayRoutineUseCase
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val UPDATE_REQUEST_CODE = 9999

class WearDaySelectionViewModel(
    private val getTodayRoutineUseCase: GetTodayRoutineUseCase? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WearDaySelectionUiState())
    val uiState: StateFlow<WearDaySelectionUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var cachedAppUpdateInfo: AppUpdateInfo? = null

    init {
        loadSessions()
    }

    fun loadSessions() {
        val useCase = getTodayRoutineUseCase ?: return
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                useCase.invoke().collect { sessionItems ->
                    _uiState.update {
                        it.copy(
                            sessions = sessionItems,
                            isLoading = false,
                        )
                    }
                }
            }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun checkForUpdates(context: Context) {
        if (_uiState.value.checkingUpdate) return

        _uiState.update {
            it.copy(
                checkingUpdate = true,
                updateMessageResId = co.japl.android.synapsefit.R.string.wear_update_checking,
            )
        }

        try {
            val appUpdateManager = AppUpdateManagerFactory.create(context.applicationContext)
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    cachedAppUpdateInfo = appUpdateInfo
                    _uiState.update {
                        it.copy(
                            checkingUpdate = false,
                            isUpdateAvailable = true,
                            updateMessageResId = co.japl.android.synapsefit.R.string.wear_update_available,
                        )
                    }
                } else {
                    cachedAppUpdateInfo = null
                    _uiState.update {
                        it.copy(
                            checkingUpdate = false,
                            isUpdateAvailable = false,
                            updateMessageResId = co.japl.android.synapsefit.R.string.wear_no_update,
                        )
                    }
                }
            }.addOnFailureListener {
                cachedAppUpdateInfo = null
                _uiState.update {
                    it.copy(
                        checkingUpdate = false,
                        isUpdateAvailable = false,
                        updateMessageResId = co.japl.android.synapsefit.R.string.wear_update_error,
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    checkingUpdate = false,
                    isUpdateAvailable = false,
                    updateMessageResId = co.japl.android.synapsefit.R.string.wear_update_error,
                )
            }
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun performImmediateUpdate(activity: android.app.Activity) {
        val info = cachedAppUpdateInfo ?: return
        try {
            val appUpdateManager = AppUpdateManagerFactory.create(activity.applicationContext)
            appUpdateManager.startUpdateFlowForResult(
                info,
                activity,
                com.google.android.play.core.appupdate.AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                UPDATE_REQUEST_CODE,
            )
        } catch (_: Exception) {
        }
    }
}
