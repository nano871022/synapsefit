package co.japl.android.synapsefit.core.usecase

import kotlinx.coroutines.flow.StateFlow

interface ISyncPendingWorkoutLogsUseCase {
    val isPhoneConnected: StateFlow<Boolean>
    val pendingSyncDataCount: StateFlow<Int>

    fun queueDataForDeferredSync(
        exerciseId: String,
        reps: Int,
        heartRateBpm: Int,
    )

    fun flushSyncQueue()
}
