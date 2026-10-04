package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import kotlinx.coroutines.flow.StateFlow

class SyncPendingWorkoutLogsUseCase(
    private val wearSyncPort: WearSyncPort,
) : ISyncPendingWorkoutLogsUseCase {
    override val isPhoneConnected: StateFlow<Boolean>
        get() = wearSyncPort.isPhoneConnected

    override val pendingSyncDataCount: StateFlow<Int>
        get() = wearSyncPort.pendingSyncDataCount

    override fun queueDataForDeferredSync(
        exerciseId: String,
        reps: Int,
        heartRateBpm: Int,
    ) {
        wearSyncPort.queueDataForDeferredSync(exerciseId, reps, heartRateBpm)
    }

    override fun flushSyncQueue() {
        wearSyncPort.flushSyncQueue()
    }
}
