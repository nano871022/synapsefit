@file:Suppress("MagicNumber")

package co.japl.android.synapsefit.ui.viewmodel

import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.SyncStepState

data class WearSyncUiState(
    val stepState: SyncStepState = SyncStepState.Idle,
    val isDisconnected: Boolean = false,
    val isPostWorkout: Boolean = false,
    val isFinished: Boolean = false,
    val activeSessionDetected: Boolean = false,
    val planId: String? = null,
    val day: Int? = null,
    val exerciseId: String? = null,
) {
    val progress: Float
        get() =
            when (stepState) {
                SyncStepState.Idle -> 0.0f
                SyncStepState.DownloadingPlans -> 0.16f
                SyncStepState.ConfiguringNextPlan -> 0.33f
                SyncStepState.VerifyingOfflineWorkouts -> 0.50f
                is SyncStepState.UploadingLogs -> {
                    val uploadState = stepState
                    if (uploadState.total <= 0) {
                        0.66f
                    } else {
                        0.50f + 0.20f * (uploadState.current.toFloat() / uploadState.total.coerceAtLeast(1))
                    }
                }
                SyncStepState.Cleanup -> 0.83f
                SyncStepState.VerifyingActiveTraining -> 0.95f
                is SyncStepState.Finished -> 1.0f
                is SyncStepState.Disconnected -> progress
            }

    val stepDescriptionResId: Int
        get() =
            when (stepState) {
                SyncStepState.Idle -> R.string.wear_sync_title
                SyncStepState.DownloadingPlans -> R.string.wear_sync_step1_downloading
                SyncStepState.ConfiguringNextPlan -> R.string.wear_sync_step2_configuring
                SyncStepState.VerifyingOfflineWorkouts -> R.string.wear_sync_step3_verifying_offline
                is SyncStepState.UploadingLogs -> {
                    if ((stepState as SyncStepState.UploadingLogs).total <= 0) {
                        R.string.wear_sync_step4_no_logs
                    } else {
                        R.string.wear_sync_step4_uploading_logs
                    }
                }
                SyncStepState.Cleanup -> R.string.wear_sync_step5_cleanup
                SyncStepState.VerifyingActiveTraining -> R.string.wear_sync_step6_verifying_active
                is SyncStepState.Finished -> R.string.wear_sync_completed
                is SyncStepState.Disconnected -> R.string.wear_sync_disconnected_prompt
            }
}
