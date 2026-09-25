package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import kotlinx.coroutines.flow.StateFlow

class SyncPendingWorkoutLogsUseCase(
    private val wearSyncPort: WearSyncPort,
) {
    val isPhoneConnected: StateFlow<Boolean>
        get() = wearSyncPort.isPhoneConnected

    val pendingSyncDataCount: StateFlow<Int>
        get() = wearSyncPort.pendingSyncDataCount

    fun queueDataForDeferredSync(
        exerciseId: String,
        reps: Int,
        heartRateBpm: Int,
    ) {
        wearSyncPort.queueDataForDeferredSync(exerciseId, reps, heartRateBpm)
    }

    fun flushSyncQueue() {
        wearSyncPort.flushSyncQueue()
    }
}
