package co.japl.android.synapsefit.core.domain.model

sealed interface SyncStepState {
    object Idle : SyncStepState

    object DownloadingPlans : SyncStepState

    object ConfiguringNextPlan : SyncStepState

    object VerifyingOfflineWorkouts : SyncStepState

    data class UploadingLogs(val current: Int, val total: Int) : SyncStepState

    object Cleanup : SyncStepState

    object VerifyingActiveTraining : SyncStepState

    data class Finished(
        val activeSessionDetected: Boolean = false,
        val planId: String? = null,
        val day: Int? = null,
        val exerciseId: String? = null,
    ) : SyncStepState

    data class Disconnected(val stepBeforeDisconnect: SyncStepState) : SyncStepState
}
